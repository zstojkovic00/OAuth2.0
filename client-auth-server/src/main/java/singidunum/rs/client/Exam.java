package singidunum.rs.client;

import java.time.LocalDate;

public record Exam(String code, String name, Double points, int grade, LocalDate date, String professor) {
}
