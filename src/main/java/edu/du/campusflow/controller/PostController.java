package edu.du.campusflow.controller;

import edu.du.campusflow.entity.Post;
import edu.du.campusflow.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;

@Controller
@RequestMapping("/iframe/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // 게시물 목록 페이지
    @GetMapping("/view")
    public String viewPosts(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model) {
        Page<Post> posts = postService.getPostsByDepartment(page, size);
        model.addAttribute("posts", posts);
        model.addAttribute("currentPage", page);
        model.addAttribute("userDepartment", postService.getCurrentUserDepartment());
        model.addAttribute("isStudent", postService.isStudent());
        return "view/iframe/posts/viewPosts";
    }

    // 게시물 추가 페이지
    @GetMapping("/add")
    public String showAddPostForm(Model model) {
        if (!postService.isStudent()) {
            throw new AccessDeniedException("학생만 게시물을 작성할 수 있습니다.");
        }
        model.addAttribute("post", new Post());
        return "view/iframe/posts/addPost";
    }

    // 게시물 추가 처리
    @PostMapping
    public String addPost(Post post) {
        postService.createPost(post);
        return "redirect:/iframe/posts/view";
    }

    // 게시물 상세 조회
    @GetMapping("/detail-view/{id}")
    public String viewPostDetail(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.getPostById(id));
        model.addAttribute("isStudent", postService.isStudent());
        return "view/iframe/posts/postDetail";
    }
    
    // 댓글 작성 처리
    @PostMapping("/{postId}/comments")
    public String addComment(@PathVariable Long postId, @RequestParam String content) {
        postService.addComment(postId, content);
        return "redirect:/iframe/posts/detail-view/" + postId;
    }
    
    // 댓글 삭제 처리
    @PostMapping("/{postId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        postService.deleteComment(commentId);
        return "redirect:/iframe/posts/detail-view/" + postId;
    }
}