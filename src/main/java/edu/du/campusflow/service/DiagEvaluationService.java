package edu.du.campusflow.service;

import edu.du.campusflow.dto.DiagEvaluationDetailDTO;
import edu.du.campusflow.dto.DiagQuestionDTO;
import edu.du.campusflow.entity.DiagItem;
import edu.du.campusflow.entity.DiagQuestion;
import edu.du.campusflow.entity.Member;
import edu.du.campusflow.entity.Ofregistration;
import edu.du.campusflow.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiagEvaluationService {
    private final DiagEvaluationRepository diagEvaluationRepository;
    private final OfregistrationRepository ofregistrationRepository;
    private final DiagQuestionRepository diagQuestionRepository;
    private final DiagItemRepository diagItemRepository;
    private final DeptRepository deptRepository;
    private final AuthService authService;

    // 교수의 강의 목록 조회 메서드 추가
    @Transactional
    public List<Map<String, Object>> getProfessorLectures() {
        Member currentMember = authService.getCurrentMember();
        List<Ofregistration> registrations = ofregistrationRepository
                .findByLectureId_Member_MemberId(currentMember.getMemberId());

        return registrations.stream()
                .map(reg -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("ofregistrationId", reg.getId());
                    map.put("lectureName", reg.getLectureId().getLectureName());
                    map.put("semester", reg.getLectureId().getSemester().getCodeName());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Map<String, Object>> getAllLectures() {
        List<Ofregistration> registrations = ofregistrationRepository.findAll();

        return registrations.stream()
                .map(reg -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("ofregistrationId", reg.getId());
                    map.put("lectureName", reg.getLectureId().getLectureName());
                    map.put("professorName", reg.getLectureId().getMember().getName());
                    map.put("semester", reg.getLectureId().getSemester().getCodeName());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<DiagQuestionDTO> getDiagnosticResults(Long ofregistrationId) {
        Ofregistration ofregistration = ofregistrationRepository.findById(ofregistrationId)
                .orElseThrow(() -> new IllegalArgumentException("수강신청 정보를 찾을 수 없습니다."));

        List<DiagQuestion> questions = diagQuestionRepository.findAll();
        List<DiagItem> diagItems = diagItemRepository.findByOfRegistration_Id(ofregistrationId);  // diagItemRepository 사용

        return questions.stream()
                .map(question -> {
                    DiagQuestionDTO dto = new DiagQuestionDTO();
                    dto.setQuestionId(question.getQuestionId());
                    dto.setQuestionName(question.getQuestionName());
                    dto.setLectureName(ofregistration.getLectureId().getLectureName());
                    dto.setName(ofregistration.getLectureId().getMember().getName());
                    dto.setSemester(ofregistration.getLectureId().getSemester().getCodeName());
                    dto.setSubjectId(ofregistration.getLectureId().getCurriculumSubject().getSubject().getSubjectId());

                    // 점수 초기화
                    dto.initializeScoreCounts();  // 추가

                    // 해당 문항에 대한 답변들 처리
                    diagItems.stream()
                            .filter(item -> item.getDiagQuestion().getQuestionId().equals(question.getQuestionId()))
                            .forEach(item -> dto.incrementScoreCount(item.getScore()));

                    dto.calculateAverageScore();  // DiagQuestionDTO에 이 메서드가 있어야 함
                    return dto;
                })
                .collect(Collectors.toList());
    }

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

    // getAllDepartments는 한 번만 정의
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
}