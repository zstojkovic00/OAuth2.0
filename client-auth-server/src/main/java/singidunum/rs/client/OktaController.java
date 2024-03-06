package singidunum.rs.client;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;


@Controller
public class OktaController {

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = Logger.getLogger(OktaController.class.getName());

    @Value("${client.id}")
    private String clientId;
    @Value("${client.secret.id}")
    private String clientSecret;

    public OktaController() {
        this.restClient = RestClient.create();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @GetMapping("/oauth2/callback")
    public ResponseEntity<Map<String, Object>> oauth2Callback(@RequestParam String code) throws IOException {
        log.info("authorization_code: " + code);

        /* LinkedMultiValueMap is used to represent request in form of `application/x-www-form-urlencoded`
            When you have multiple values per one key, you can use HashMap also
         */
        var body = new LinkedMultiValueMap<String, String>();
        body.add("code", code);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", "http://localhost:3000");

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

        Map<String, Object> response = new HashMap<>();
        response.put("username", decodedPayload.get("name"));
        response.put("attributes", decodedPayload);
        response.put("access_token", parsedResponse.accessToken());
        return ResponseEntity.ok(response);
    }


    private String getCredentials() {
        var credentialsString = "%s:%s".formatted(clientId, clientSecret);
        return Base64.getUrlEncoder().encodeToString(credentialsString.getBytes());
    }

//    @GetMapping("/exams")
//    public String conferences(Model model, HttpServletRequest request) {
//        var session = request.getSession();
//        if (session != null && session.getAttribute("access_token") != null) {
//            try {
//                var exams = this.restClient.get()
//                        .uri("http://localhost:8081/exams?userId")
//                        .header("authorization", "Bearer " + session.getAttribute("access_token"))
//                        .accept(MediaType.APPLICATION_JSON)
//                        .retrieve()
//                        .body(List.class);
//                model.addAttribute("exams", exams);
//            } catch (Exception e) {
//                model.addAttribute("error", "Error while getting conferences: " + e.getMessage());
//            }
//
//            return "exams";
//        }
//
//        return "redirect:/";
//    }
}
