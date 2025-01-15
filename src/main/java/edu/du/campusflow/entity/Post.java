package edu.du.campusflow.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY) // Dept와의 Many-to-One 관계
    @JoinColumn(name = "dept_id") // 외래 키 설정
    private Dept dept; // 학과 엔티티

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY) // 자기 참조 관계
    @JoinColumn(name = "related_post") // 외래 키 설정
    private Post relatedPost; // 관련 게시물 엔티티 (댓글)

    @Column(name = "title", length = 100)
    private String title; // 제목

    @Column(name = "content", columnDefinition = "TEXT")
    private String content; // 내용

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 생성 날짜

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 업데이트 날짜

    @OneToMany(mappedBy = "relatedPost", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Post> comments; // 댓글 목록


}