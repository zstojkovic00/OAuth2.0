package singidunum.rs.client;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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

    public OktaController() {
        this.restClient = RestClient.create();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Value("${client.id}")
    private String clientId;
    @Value("${client.secret.id}")
    private String clientSecret;

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

    @GetMapping("/exams")
    public ResponseEntity<List<Exam>> exams(@RequestHeader("Authorization") String auth) {
        try {
            if (auth == null || !auth.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String accessToken = auth.substring(7).replace("\"", "");

            List<Exam> exams = this.restClient.get()
                    .uri("http://localhost:8081/exams?userId")
                    .header("Authorization", "Bearer " + accessToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            return new ResponseEntity<>(exams, HttpStatus.OK);
        } catch (Exception e) {
            log.severe("Error while getting exams: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

