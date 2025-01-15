package edu.du.campusflow.repository;

import edu.du.campusflow.entity.Dept;
import edu.du.campusflow.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // 학과별 게시물 조회 (생성일 기준 내림차순, 댓글 제외, 페이징 적용)
    Page<Post> findByDeptAndRelatedPostIsNullOrderByCreatedAtDesc(Dept dept, Pageable pageable);
}