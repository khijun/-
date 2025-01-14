package edu.du.campusflow.repository;

import edu.du.campusflow.entity.Member;
import edu.du.campusflow.entity.Ofregistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfregistrationRepository extends JpaRepository<Ofregistration, Long> {

    List<Ofregistration> findDistinctByLectureId_Member(Member member);
    List<Ofregistration> findByLectureId_LectureId(Long lectureId);

    @Query(value = "SELECT " +
            "l.lecture_name AS lectureName, " +
            "dq.question_name AS questionName, " +
            "di.score AS score " +
            "FROM ofregistration o " +
            "INNER JOIN lecture l ON o.lecture_id = l.lecture_id " +
            "INNER JOIN curriculum_subject cs ON l.curriculum_subject_id = cs.curriculum_subject_id " +
            "INNER JOIN curriculum c ON cs.curriculum_id = c.curriculum_id " +
            "INNER JOIN dept d ON c.dept_id = d.dept_id " +
            "INNER JOIN common_code sem ON l.semester = sem.code_id " +
            "INNER JOIN member m ON o.member_id = m.member_id " +  // member 명시적 조인
            "INNER JOIN diag_items di ON di.ofregistration_id = o.id " +
            "INNER JOIN diag_questions dq ON di.question_id = dq.question_id " +
            "WHERE (:deptId IS NULL OR d.dept_id = :deptId) " +
            "AND (:grade IS NULL OR sem.code_value = :grade) " +
            "AND (:lectureName IS NULL OR l.lecture_name LIKE CONCAT('%', :lectureName, '%')) " +
            "AND (:studentName IS NULL OR m.name LIKE CONCAT('%', :studentName, '%')) " +
            "ORDER BY l.lecture_name, dq.question_name",
            nativeQuery = true)
    List<Object[]> findEvaluationsBySearchCriteria(
            @Param("deptId") Long deptId,
            @Param("grade") String grade,
            @Param("lectureName") String lectureName,
            @Param("studentName") String studentName
    );

    @Query("SELECT o FROM Ofregistration o WHERE o.lectureId.member.memberId = :memberId")
    List<Ofregistration> findByLectureId_Member_MemberId(@Param("memberId") Long memberId);

    @Query("SELECT o FROM Ofregistration o WHERE o.lectureId.member.dept.deptId = :deptId")
    List<Ofregistration> findByMemberDeptId(@Param("deptId") String deptId);

}