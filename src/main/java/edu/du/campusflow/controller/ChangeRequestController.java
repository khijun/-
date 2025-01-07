package edu.du.campusflow.controller;

import edu.du.campusflow.dto.ChangeRequestDto;
import edu.du.campusflow.entity.ChangeRequest;
import edu.du.campusflow.entity.CommonCode;
import edu.du.campusflow.entity.Member;
import edu.du.campusflow.repository.CommonCodeRepository;
import edu.du.campusflow.service.AuthService;
import edu.du.campusflow.service.ChangeRequestService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ChangeRequestController {

    private final ChangeRequestService changeRequestService;
    private final AuthService authService;
    private final CommonCodeRepository commonCodeRepository;

    public ChangeRequestController(ChangeRequestService changeRequestService, AuthService authService, CommonCodeRepository commonCodeRepository) {
        this.changeRequestService = changeRequestService;
        this.authService = authService;
        this.commonCodeRepository = commonCodeRepository;
    }

    // 관리자용 - 모든 학생의 신청서 조회
    @GetMapping("/iframe/academic/admin/admin-change-request-list")
    public String getAllChangeRequests(Model model) {
        List<ChangeRequest> changeRequests = changeRequestService.getALlChangeRequests();  // 모든 학생의 신청서 조회
        model.addAttribute("changeRequests", changeRequests);
        return "view/iframe/academic/admin/admin-change-request-list"; // 관리자용 변동 신청 목록 페이지
    }
    // 학적 조회
    @GetMapping("/iframe/academic/change-request-list")
    public String getChangeRequests(Model model) {
        List<ChangeRequest> changeRequests = changeRequestService.getChangeRequestsByMemberId(authService.getCurrentMemberId());
        model.addAttribute("changeRequests", changeRequests);
        return "view/iframe/academic/change-request-list"; // 학적 조회 페이지
    }

    // 휴학/복학 신청 폼
    @GetMapping("/iframe/academic/change-request-form")
    public String changeRequestForm(Model model) {
        model.addAttribute("changeRequestDto", new ChangeRequestDto());
        model.addAttribute("authService", authService);
        return "view/iframe/academic/change-request-form"; // 휴학/복학 신청 페이지
    }

    // 휴학/복학 신청 처리
    @PostMapping("/iframe/academic/process-change-request")
    public String processChangeRequest(@ModelAttribute ChangeRequestDto changeRequestDto) {
        Long memberId = authService.getCurrentMemberId();
        if (memberId == null) {
            throw new IllegalStateException("Authentication required to perform this action.");
        }

        // DTO를 서비스에 전달하여 처리
        changeRequestService.processChangeRequest(changeRequestDto);
        System.out.println(authService.getCurrentMemberId());
        return "redirect:/iframe/academic/change-request-list"; // 신청 후 목록으로 리다이렉트
    }

    // 신청 삭제
    @PostMapping("/iframe/academic/delete-change-request")
    public String deleteChangeRequest(@RequestParam("applicationId") Long applicationId) {
        Long memberId = authService.getCurrentMemberId();
        if (memberId == null) {
            throw new IllegalStateException("Authentication required to perform this action.");
        }

        // 신청서 삭제 처리
        changeRequestService.deleteChangeRequest(applicationId, memberId);

        return "redirect:/iframe/academic/change-request-list"; // 삭제 후 목록으로 리다이렉트
    }

    // 승인 처리
    @PostMapping("/iframe/admin/academic/approve-change-request/{applicationId}")
    public String approveChangeRequest(@PathVariable("applicationId") Long applicationId) {
        Long memberId = authService.getCurrentMemberId();
        if (memberId == null) {
            throw new IllegalStateException("Authentication required to perform this action.");
        }

        // 신청서 승인 처리
        changeRequestService.handleChangeRequest(applicationId, true);

        return "redirect:/iframe/academic/admin/admin-change-request-list"; // 승인 후 목록으로 리다이렉트
    }

    // 거절 처리
    @PostMapping("/iframe/admin/academic/reject-change-request/{applicationId}")
    public String rejectChangeRequest(@PathVariable("applicationId") Long applicationId) {
        Long memberId = authService.getCurrentMemberId();
        if (memberId == null) {
            throw new IllegalStateException("Authentication required to perform this action.");
        }

        // 신청서 거절 처리
        changeRequestService.handleChangeRequest(applicationId, false);

        return "redirect:/iframe/academic/admin/admin-change-request-list"; // 거절 후 목록으로 리다이렉트
    }
}