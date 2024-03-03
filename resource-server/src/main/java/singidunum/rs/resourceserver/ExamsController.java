package singidunum.rs.resourceserver;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import singidunum.rs.resourceserver.AccessToken.Payload;

import java.io.IOException;
import java.time.Instant;
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
    private ResponseEntity<List<Exam>> findAllPassedExams(@RequestHeader String authorization, String userId) throws IOException {
        Payload payload = parseAccessToken(authorization);

        validatePayload(payload);


        List<Exam> passedExams = examService.findAll("-1");
        return new ResponseEntity<>(passedExams, HttpStatus.OK);
    }

    private void validatePayload(Payload payload) {
        if (Instant.ofEpochSecond(payload.expiry()).isBefore(Instant.now())) {
            throw new RuntimeException("Expired");
        }
//        if (!payload.scopes().contains("exams.list")) {
//            throw new RuntimeException("You dont have right permissions");
//        }
    }

    private Payload parseAccessToken(String authorization) throws IOException {
        String accessToken = authorization.replace("Bearer ", "");
        log.info("Access token " + accessToken);
        var parts = accessToken.split("\\.");
        return objectMapper.readValue(Base64.getUrlDecoder().decode(parts[1].getBytes()), Payload.class);
    }
}
