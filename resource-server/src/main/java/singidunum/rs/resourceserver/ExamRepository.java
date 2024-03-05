package singidunum.rs.resourceserver;

import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;


@Repository
public class ExamRepository {

    private final MultiValueMap<String, Exam> exams;

    public ExamRepository() {
        this.exams = new LinkedMultiValueMap<>();
        exams.add("zstojkovic00@github.oktaidp", new Exam("RSD1CC", "Cloud computing", 98.00, 10, LocalDate.of(2023, Month.JANUARY, 23), "Nebojša Bačanin Džakula"));
        exams.add("zstojkovic00@github.oktaidp", new Exam("RSD1CC", "Cloud computing", 98.00, 10, LocalDate.of(2023, Month.JANUARY, 23), "Nebojša Bačanin Džakula"));
        exams.add("zstojkovic00@github.oktaidp", new Exam("RSD1CC", "Cloud computing", 98.00, 10, LocalDate.of(2023, Month.JANUARY, 23), "Nebojša Bačanin Džakula"));
        exams.add("-1", new Exam("RSD1CC", "Cloud computing", 98.00, 10, LocalDate.of(2022, Month.JANUARY, 23), "Nebojša Bačanin Džakula"));

    }

    public List<Exam> findAll(String userId) {
        return this.exams.get(userId);
    }
}
