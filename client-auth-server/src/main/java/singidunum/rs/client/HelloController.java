package singidunum.rs.client;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;


@Controller
public class HelloController {

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = Logger.getLogger(HelloController.class.getName());

    @Value("${client.id}")
    private String clientId;
    @Value("${client.secret.id}")
    private String clientSecret;

    public HelloController() {
        this.restClient = RestClient.create();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

//    @GetMapping("/")
//    public String index(HttpServletRequest request, Model model) {
//        var session = request.getSession();
//        if (session != null && session.getAttribute("username") != null) {
//            model.addAttribute("username", session.getAttribute("username"));
//            model.addAttribute("attributes", session.getAttribute("attributes"));
//            return "authenticated";
//        } else {
//            var loginUri = UriComponentsBuilder.fromHttpUrl("https://dev-80556277.okta.com/oauth2/default/v1/authorize")
//                    .queryParam("redirect_uri", "http://localhost:8080/oauth2/callback")
//                    .queryParam("response_type", "code")
//                    .queryParam("state", UUID.randomUUID())
//                    .queryParam("scope", "openid email profile")
//                    .queryParam("client_id", clientId)
//                    .build();
//            model.addAttribute("loginUri", loginUri);
//
//            return "anonymous";
//        }
//    }
//
//    @PostMapping("/login")
//    public String login(HttpServletRequest request) {
//        var session = request.getSession(true);
//        session.setAttribute("username", "Zeljko");
//        request.getSession().setAttribute("attributes",
//                Map.of(
//                        "firstName", "Zeljko",
//                        "lastName", "Stojkovic",
//                        "company", "Yettel",
//                        "userType", "hardcoded"
//                )
//        );
//        return "redirect:/";
//    }
//
//    @PostMapping("/logout")
//    public String logout(HttpServletRequest request) {
//        var session = request.getSession();
//        if (session != null) {
//            session.invalidate();
//        }
//        return "redirect:/";
//    }


    @GetMapping("/exams")
    public String conferences(Model model, HttpServletRequest request) {
        var session = request.getSession();
        if (session != null && session.getAttribute("access_token") != null) {
            try {
                var exams = this.restClient.get()
                        .uri("http://localhost:8081/exams?userId")
                        .header("authorization", "Bearer " + session.getAttribute("access_token"))
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .body(List.class);
                model.addAttribute("exams", exams);
            } catch (Exception e) {
                model.addAttribute("error", "Error while getting conferences: " + e.getMessage());
            }

            return "exams";
        }

        return "redirect:/";
    }

    @GetMapping("/oauth2/callback")
    public String oauth2Callback(@RequestParam String code, HttpServletRequest request) throws IOException {
        log.info("authorization_code: " + code);
        // form encoded URL string
        var body = new LinkedMultiValueMap<String, String>();
        body.add("code", code);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", "http://localhost:8080/oauth2/callback");


        String token = restClient.post()
                .uri("https://dev-80556277.okta.com/oauth2/default/v1/token")
                .header("Authorization", "Basic " + getCredentials())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(String.class);

        log.info("Token response: " + token);
        var parsedResponse = objectMapper.readValue(token, TokenResponse.class);
        log.info("token id: " + parsedResponse.idToken());

        String[] tokenParts = parsedResponse.idToken().split("\\.");
        byte[] payload = Base64.getUrlDecoder().decode(tokenParts[1]);
        log.info("payload: " + new String(payload));

        var decodedPayload = objectMapper.readValue(payload, Map.class);

        var session = request.getSession(true);
        session.setAttribute("username", decodedPayload.get("name"));
        session.setAttribute("attributes", decodedPayload);
        session.setAttribute("access_token", parsedResponse.accessToken());

        return "redirect:/";
    }

    private String getCredentials() {
        var credentialsString = "%s:%s".formatted(clientId, clientSecret);
        return Base64.getUrlEncoder().encodeToString(credentialsString.getBytes());
    }
}
