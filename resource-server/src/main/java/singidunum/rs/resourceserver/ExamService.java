package singidunum.rs.resourceserver;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamService {

    private final ExamRepository examRepository;

    public ExamService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    public List<Exam> findAll(String userId) {
       return examRepository.findAll(userId);
    }
}
