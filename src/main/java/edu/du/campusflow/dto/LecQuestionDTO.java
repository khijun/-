package edu.du.campusflow.dto;

import lombok.Data;

@Data
public class LecQuestionDTO {
    private Long questionId;
    private String questionName;
    private double averageScore;

    // 강의 정보 필드 추가
    private String lectureName;    // 과목명
    private String name;  // 교수명
    private String semester;       // 학기
    private Long subjectId;    // 과목 코드

    // 각 점수별 응답 수
    private long score5Count;
    private long score4Count;
    private long score3Count;
    private long score2Count;
    private long score1Count;

    // 각 점수별 비율
    private double score5Percent;
    private double score4Percent;
    private double score3Percent;
    private double score2Percent;
    private double score1Percent;
}
