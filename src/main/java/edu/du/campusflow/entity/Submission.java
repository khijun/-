package edu.du.campusflow.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Table(name = "submission")
public class Submission { //학생이 제출한 과제 데이터를 저장하는 엔티티

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    private Long submissionId; //과제 제출 아이디

    @ManyToOne
    @JoinColumn(name = "ofregistration_id")
    private Ofregistration ofregistration; //수강신청 아이디

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment; //과제 아이디

    @Column(name = "submission_date")
    private LocalDateTime submissionDate;  //과제 제출일

    @Column(name = "assignment_score")
    private Integer assignmentScore; //과제 점수

    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileInfo fileInfo; //파일 아이디
}
