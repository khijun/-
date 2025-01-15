package edu.du.campusflow.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inquiry")
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long inquiryId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "subject", length = 100)
    private String subject;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "inquiry_status",referencedColumnName = "code_id")
    private CommonCode inquiryStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "related_inquiry")
    private Inquiry relatedInquiry;        // 이 문의가 답변하는 원본 문의

    @OneToOne(mappedBy = "relatedInquiry")
    private Inquiry response;              // 이 문의에 대한 답변




}