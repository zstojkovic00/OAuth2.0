package singidunum.rs.client;


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

import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;


@Controller
public class HelloController {

    private final RestClient restClient;
    private static final Logger log = Logger.getLogger(HelloController.class.getName());

    @Value("${client.id}")
    private String clientId;
    @Value("${client.secret.id}")
    private String clientSecret;

    public HelloController() {
        this.restClient = RestClient.create();
    }

    @GetMapping("/")
    public String index(HttpServletRequest request, Model model) {
        var session = request.getSession();
        if (session != null && session.getAttribute("username") != null) {
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("attributes", session.getAttribute("attributes"));
            return "authenticated";
        } else {
            var loginUri = UriComponentsBuilder.fromHttpUrl("https://dev-80556277.okta.com/oauth2/default/v1/authorize")
                    .queryParam("redirect_uri", "http://localhost:8080/oauth2/callback")
                    .queryParam("response_type", "code")
                    .queryParam("state", UUID.randomUUID())
                    .queryParam("scope", "openid email profile")
                    .queryParam("client_id", clientId)
                    .build();
            model.addAttribute("loginUri", loginUri);

            return "anonymous";
        }
    }

    @PostMapping("/login")
    public String login(HttpServletRequest request) {
        var session = request.getSession(true);
        session.setAttribute("username", "Zeljko");
        request.getSession().setAttribute("attributes",
                Map.of(
                        "firstName", "Zeljko",
                        "lastName", "Stojkovic",
                        "company", "Yettel",
                        "userType", "hardcoded"
                )
        );
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        var session = request.getSession();
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    @GetMapping("/oauth2/callback")
    public String oauth2Callback(@RequestParam String code) {
        log.info("authorization_code: " + code);
        // form encoded URL string
        var payload = new LinkedMultiValueMap<String, String>();
        payload.add("code", code);
        payload.add("grant_type", "authorization_code");
        payload.add("redirect_uri", "http://localhost:8080/oauth2/callback");


        String token = restClient.post()
                .uri("https://dev-80556277.okta.com/oauth2/default/v1/token")
                .header("Authorization", "Basic " + getCredentials())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(payload)
                .retrieve()
                .body(String.class);

        log.info("Token response: " + token);
        return "redirect:/";
    }

    private String getCredentials() {
        var credentialsString = "%s:%s".formatted(clientId, clientSecret);
        return Base64.getUrlEncoder().encodeToString(credentialsString.getBytes());
    }
}
