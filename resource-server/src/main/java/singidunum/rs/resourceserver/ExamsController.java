package singidunum.rs.resourceserver;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import singidunum.rs.resourceserver.AccessToken.Header;
import singidunum.rs.resourceserver.AccessToken.Payload;
import singidunum.rs.resourceserver.AccessToken.Jwt;

import java.io.IOException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

@RestController
public class ExamsController {
    private final ExamService examService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger log = Logger.getLogger(ExamsController.class.getName());

    public ExamsController(ExamService examService) {
        this.examService = examService;
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @GetMapping("/exams")
    private ResponseEntity<List<Exam>> findAllPassedExams(@RequestHeader("Authorization") String auth) throws Exception {

        var authJwt = auth.substring(7);
        var decodedToken = decodeJwt(authJwt);

        // The Resource Server has to verify that the token hasn’t been manipulated by checking its signature as well as validate its claims
        var keys = JwkUtils.getKeys("https://dev-80556277.okta.com/oauth2/default/v1/keys");
        log.info(keys.toString());
        var kid = decodedToken.header().kid();
        var publicKey = keys.stream().filter(jwk -> jwk.kid().equals(kid))
                .findFirst()
                .get()
                .publicKey();
        var isValid = JwkUtils.verifySignature(publicKey, decodedToken.signedData(), decodedToken.signature());
        if (!isValid) {
            throw new RuntimeException("invalid access token");
        }

        var expiry = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(decodedToken.payload().expiry()),
                ZoneId.systemDefault()
        );
        if (expiry.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Expired access_token");
        }

        var scopes = decodedToken.payload().scopes();
        log.info(scopes.toString());
        if (!scopes.contains("singidunum.read")) {
            throw new RuntimeException("You don't have right scopes");
        }

        var userId = decodedToken.payload().subject();


        List<Exam> passedExams = examService.findAll(userId);
        return new ResponseEntity<>(passedExams, HttpStatus.OK);
    }

//    public JwtTest decodeJwtTest(String authJwt) throws ParseException {
//        SignedJWT signedJWT = JwtUtilsTest.parseSignedJwt(authJwt);
//        JWSHeader header = signedJWT.getHeader();
//        Payload payload = signedJWT.getPayload();
//        byte[] signedData = JwtUtilsTest.extractSignedData(signedJWT);
//        byte[] signature = JwtUtilsTest.extractSignature(signedJWT);
//
//        return new JwtTest(header, payload, signedData, signature);
//    }

    private Jwt decodeJwt(String token) throws IOException {
        var parts = token.split("\\.");
        return new Jwt(
                objectMapper.readValue(Base64.getUrlDecoder().decode(parts[0]), Header.class),
                objectMapper.readValue(Base64.getUrlDecoder().decode(parts[1]), Payload.class),
                token.substring(0, token.lastIndexOf(".")).getBytes(),
                Base64.getUrlDecoder().decode(parts[2])
        );
    }


}
