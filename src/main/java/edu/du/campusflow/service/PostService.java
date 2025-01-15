package edu.du.campusflow.service;

import edu.du.campusflow.entity.Dept;
import edu.du.campusflow.entity.Member;
import edu.du.campusflow.entity.Post;
import edu.du.campusflow.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private AuthService authService;

    // 현재 사용자의 학과 게시물 조회 (댓글 제외, 페이징 적용)
    public Page<Post> getPostsByDepartment(int page, int size) {
        Member currentMember = authService.getCurrentMember();
        if (currentMember == null || currentMember.getDept() == null) {
            throw new IllegalStateException("학과 정보가 없습니다.");
        }
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findByDeptAndRelatedPostIsNullOrderByCreatedAtDesc(currentMember.getDept(), pageable);
    }

    // 특정 게시물 조회
    public Post getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시물을 찾을 수 없습니다."));
                
        // 현재 사용자가 같은 학과인지 확인
        Member currentMember = authService.getCurrentMember();
        if (currentMember == null || currentMember.getDept() == null ||
            !currentMember.getDept().equals(post.getDept())) {
            throw new IllegalStateException("해당 게시물에 접근할 수 없습니다.");
        }
        
        return post;
    }

    // 게시물 생성
    public Post createPost(Post post) {
        Member currentMember = authService.getCurrentMember();
        if (currentMember == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        
        if (currentMember.getDept() == null) {
            throw new IllegalStateException("학과 정보가 없습니다.");
        }

        // 현재 시간 설정
        post.setCreatedAt(LocalDateTime.now());
        
        // 작성자와 학과 설정
        post.setMember(currentMember);
        post.setDept(currentMember.getDept());

        return postRepository.save(post);
    }

    // 학생 여부 확인
    public boolean isStudent() {
        Member currentMember = authService.getCurrentMember();
        if (currentMember == null || currentMember.getMemberType() == null) {
            return false;
        }
        return "STUDENT".equals(currentMember.getMemberType().getCodeValue());
    }

    // 현재 사용자의 학과 정보 조회
    public Dept getCurrentUserDepartment() {
        Member currentMember = authService.getCurrentMember();
        if (currentMember == null) {
            return null;
        }
        return currentMember.getDept();
    }
    
    // 댓글 작성
    public void addComment(Long postId, String content) {
        Post post = getPostById(postId);
        
        Member currentMember = authService.getCurrentMember();
        if (currentMember == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        
        Post comment = Post.builder()
                .content(content)
                .createdAt(LocalDateTime.now())
                .member(currentMember)
                .dept(currentMember.getDept())
                .relatedPost(post)
                .build();
        
        postRepository.save(comment);
    }
    
    // 댓글 삭제
    public void deleteComment(Long commentId) {
        Post comment = postRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));
        
        Member currentMember = authService.getCurrentMember();
        if (currentMember == null || !currentMember.equals(comment.getMember())) {
            throw new IllegalStateException("댓글 삭제 권한이 없습니다.");
        }
        
        postRepository.delete(comment);
    }
    
    // 현재 사용자가 댓글 작성자인지 확인
    public boolean isCommentAuthor(Post comment) {
        Member currentMember = authService.getCurrentMember();
        return currentMember != null && currentMember.equals(comment.getMember());
    }
}