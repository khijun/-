package edu.du.campusflow.service;

import edu.du.campusflow.dto.DiagEvaluationDetailDTO;
import edu.du.campusflow.dto.DiagQuestionDTO;
import edu.du.campusflow.entity.DiagItem;
import edu.du.campusflow.entity.Member;
import edu.du.campusflow.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiagEvaluationService {
    private final DiagEvaluationRepository diagEvaluationRepository;
    private final DeptRepository deptRepository;
    private final AuthService authService;
    private final OfregistrationRepository ofregistrationRepository;
    private final DiagItemRepository diagItemRepository;
    private final DiagQuestionRepository diagQuestionRepository;

    @Transactional
    public List<DiagEvaluationDetailDTO> searchEvaluations(
            Long deptId, String grade, String lectureName, String name) {

        Long gradeCodeId = getGradeCodeId(grade);  // 학년을 코드 ID로 변환

        return diagEvaluationRepository.findEvaluations(
                deptId,
                gradeCodeId,  // 변환된 코드 ID 전달
                lectureName,
                name
        );
    }

    private Long getGradeCodeId(String grade) {
        switch (grade) {
            case "1": return 97L;  // 1학년
            case "2": return 98L;  // 2학년
            case "3": return 99L;  // 3학년
            case "4": return 100L; // 4학년
            default: throw new IllegalArgumentException("Invalid grade: " + grade);
        }
    }

    public List<Map<String, Object>> getAllDepartments() {
        return deptRepository.findAll().stream()
                .map(dept -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("deptId", dept.getDeptId());
                    map.put("deptName", dept.getDeptName());
                    return map;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getProfessorDepartment() {
        Member professor = authService.getCurrentMember();  // authService 사용

        Map<String, Object> deptMap = new HashMap<>();
        deptMap.put("deptId", professor.getDept().getDeptId());
        deptMap.put("deptName", professor.getDept().getDeptName());

        return Collections.singletonList(deptMap);
    }

    // 학생의 수강 강의 목록과 진단평가 여부 조회
    public List<Map<String, Object>> getStudentLecturesWithEvalStatus(Long studentId) {
        log.info("학생 수강 강의 목록 조회 - studentId: {}", studentId);
        return ofregistrationRepository.findLecturesWithEvalStatus(studentId);
    }

    // 진단평가 여부 확인
    public boolean isAlreadyEvaluated(Long ofregistrationId) {
        return diagItemRepository.existsByOfRegistration_Id(ofregistrationId);
    }

    // 진단평가 문항 조회
    public List<DiagQuestionDTO> getDiagQuestions(Long ofregistrationId) {
        log.info("진단평가 문항 조회 - ofregistrationId: {}", ofregistrationId);
        // findByOfregistration_Id -> findByOfRegistration_Id로 수정
        return diagQuestionRepository.findByOfRegistration_Id(ofregistrationId)
                .stream()
                .map(question -> DiagQuestionDTO.builder()
                        .questionId(question.getQuestionId())
                        .questionName(question.getQuestionName())
                        .build())
                .collect(Collectors.toList());
    }

    public Map<Long, Integer> getPreviousAnswers(Long ofregistrationId) {
        List<DiagItem> diagItems = diagItemRepository.findByOfRegistration_Id(ofregistrationId);
        return diagItems.stream()
                .collect(Collectors.toMap(
                        item -> item.getDiagQuestion().getQuestionId(),
                        DiagItem::getScore
                ));
    }
}