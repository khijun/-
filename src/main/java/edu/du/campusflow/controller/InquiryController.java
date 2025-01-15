package edu.du.campusflow.controller;

import edu.du.campusflow.entity.Inquiry;
import edu.du.campusflow.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.AccessDeniedException;

@Controller
@RequestMapping("/iframe/inquiry")
public class InquiryController {

    @Autowired
    private InquiryService inquiryService;

    // 문의 추가 페이지
    @GetMapping("/add")
    public String showAddInquiryForm() {
        // 학생이 아닌 경우 접근 거부
        if (!inquiryService.isStudent()) {
            throw new AccessDeniedException("학생만 문의사항을 작성할 수 있습니다.");
        }
        return "/view/iframe/inquiry/addInquiry"; // 문의 추가 페이지로 이동
    }

    // 문의 추가 처리
    @PostMapping
    public String addInquiry(Inquiry inquiry) {
        inquiryService.createInquiry(inquiry); // 문의 생성
        return "redirect:/iframe/inquiry/view"; // 문의 추가 후 목록 페이지로 리다이렉트
    }

    // 모든 문의 조회 페이지
    @GetMapping("/view")
    public String viewInquiries(Model model) {
        model.addAttribute("inquiries", inquiryService.getInquiriesByUserRole()); // 사용자 권한에 따른 문의사항 조회
        model.addAttribute("isStaff", inquiryService.isStaff()); // 교직원 여부 추가
        model.addAttribute("isStudent", inquiryService.isStudent()); // 학생 여부 추가
        return "/view/iframe/inquiry/viewInquiries"; // 문의 목록 페이지로 이동
    }

    // 특정 문의 상세 조회 페이지
    @GetMapping("detail-view/{id}")
    public String showInquiryDetail(@PathVariable Long id, Model model) {
        Inquiry inquiry = inquiryService.getInquiryById(id);
        
        // 작성자나 교직원이 아닌 경우 접근 거부
        if (!inquiryService.isAuthor(inquiry) && !inquiryService.isStaff()) {
            throw new AccessDeniedException("해당 문의사항에 대한 접근 권한이 없습니다.");
        }

        model.addAttribute("inquiry", inquiry);
        model.addAttribute("isAuthor", inquiryService.isAuthor(inquiry));
        model.addAttribute("isStaff", inquiryService.isStaff());

        return "view/iframe/inquiry/inquiryDetail";
    }
    @PostMapping("/{id}/complete")
    public String completeInquiry(@PathVariable Long id) {
        try{
            inquiryService.completeInquiry(id);
            return "redirect:/iframe/inquiry/detail-view/" + id;
        }catch(IllegalStateException e){
            return "redirect:/iframe/inquiry/delete-view/" + id;
        }
    }
    @PostMapping("/{id}/reply")
    public String addInquiryReply(@PathVariable Long id, Inquiry response) {
        try {
            inquiryService.addResponse(id, response);
            return "redirect:/iframe/inquiry/detail-view/" + id;
        }catch(IllegalStateException e){
            return "redirect:/iframe/inquiry/delete-view/" + id;
        }
    }
}