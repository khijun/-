package edu.du.campusflow.service;

import edu.du.campusflow.entity.CommonCode;
import edu.du.campusflow.entity.Inquiry;
import edu.du.campusflow.entity.Member;
import edu.du.campusflow.repository.CommonCodeRepository;
import edu.du.campusflow.repository.InquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class InquiryService {
    @Autowired
    private InquiryRepository inquiryRepository;
    @Autowired
    private CommonCodeRepository commonCodeRepository;
    @Autowired
    private AuthService authService;

    // 사용자 권한에 따른 문의 조회
    public List<Inquiry> getInquiriesByUserRole() {
        var currentMember = authService.getCurrentMember();
        if (currentMember == null) {
            return Collections.emptyList();
        }

        // 교직원인 경우 모든 문의사항 조회
        if (isStaff()) {
            return getAllInquiries();
        }
        
        // 학생인 경우 자신의 문의사항만 조회
        return inquiryRepository.findByMemberAndRelatedInquiryIsNullOrderByCreatedAtDesc(currentMember);
    }

    // 모든 문의 조회 (교직원용)
    public List<Inquiry> getAllInquiries() {
        // 원본 문의사항만 조회 (relatedInquiry가 null인 것만)
        List<Inquiry> inquiries = inquiryRepository.findByRelatedInquiryIsNullOrderByCreatedAtDesc();

        // 기본 상태 코드 조회
        CommonCode defaultStatus = commonCodeRepository.findByCodeValue("AWAITING");
        if (defaultStatus == null) {
            throw new RuntimeException("기본 상태 코드를 찾을 수 없습니다.");
        }

        // 각 문의사항의 상태 확인 및 설정
        for (Inquiry inquiry : inquiries) {
            if (inquiry.getInquiryStatus() == null) {
                inquiry.setInquiryStatus(defaultStatus);
                inquiryRepository.save(inquiry);
            }
        }

        return inquiries;
    }

    public Inquiry getInquiryById(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new EntityNotFoundException("문의사항을 찾을 수 없습니다."));

        // 문의 상태가 null인 경우 기본값 설정
        if (inquiry.getInquiryStatus() == null) {
            CommonCode defaultStatus = commonCodeRepository.findByCodeValue("AWAITING");
            if (defaultStatus == null){
                throw new RuntimeException("기본 상태 코드를 찾을 수 없습니다.");
            }
            inquiry.setInquiryStatus(defaultStatus);
            inquiryRepository.save(inquiry);
        }

        return inquiry;
    }

    //작성자 확인
    public boolean isAuthor(Inquiry inquiry) {
        var currentMember = authService.getCurrentMember();
        if (currentMember == null || inquiry.getMember() == null) {
            return false;
        }
        return inquiry.getMember().getMemberId().equals(currentMember.getMemberId());
    }

    // 교직원 여부 확인
    public boolean isStaff() {
        var currentMember = authService.getCurrentMember();
        if (currentMember == null || currentMember.getMemberType() == null) {
            return false;
        }
        return "ADMIN".equals(currentMember.getMemberType().getCodeValue());
    }

    // 학생 여부 확인
    public boolean isStudent() {
        var currentMember = authService.getCurrentMember();
        if (currentMember == null || currentMember.getMemberType() == null) {
            return false;
        }
        return "STUDENT".equals(currentMember.getMemberType().getCodeValue());
    }

    // 문의 생성
    public Inquiry createInquiry(Inquiry inquiry) {
        // 현재 로그인한 사용자 정보 가져오기
        Member currentMember = authService.getCurrentMember();
        if (currentMember == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        // 현재 시간 설정
        inquiry.setCreatedAt(LocalDateTime.now());

        // 작성자 설정
        inquiry.setMember(currentMember);

        // 기본 상태 코드 조회
        CommonCode awaitingStatus = commonCodeRepository.findByCodeValue("AWAITING");
        if (awaitingStatus == null) {
            throw new RuntimeException("대기 상태 코드를 찾을 수 없습니다.");
        }
        inquiry.setInquiryStatus(awaitingStatus);

        return inquiryRepository.save(inquiry);
    }

    // 문의 완료 처리
    public void completeInquiry(Long inquiryId) {
        Inquiry inquiry = getInquiryById(inquiryId);

        if (!isAuthor(inquiry)) {
            throw new IllegalStateException("작성자만 완료 처리할 수 있습니다.");
        }

        if (!"PROCESSING".equals(inquiry.getInquiryStatus().getCodeValue()) || 
            inquiry.getResponse() == null) {
            throw new IllegalStateException("답변이 등록되고 처리중 상태인 문의사항만 완료 처리가 가능합니다.");
        }

        CommonCode completedStatus = commonCodeRepository.findByCodeValue("PROCESSED");
        if (completedStatus == null) {
             throw new RuntimeException("완료 상태 코드를 찾을 수 없습니다.");
        }

        inquiry.setInquiryStatus(completedStatus);
        inquiryRepository.save(inquiry);
    }

    // 문의 삭제
    public void deleteInquiry(Long inquiryId) {
        inquiryRepository.deleteById(inquiryId);
    }

    public Inquiry addResponse(Long inquiryId, Inquiry response) {
        if (!isStaff()) {
            throw new IllegalStateException("교직원만 답변을 등록할 수 있습니다.");
        }

        Inquiry existingInquiry = getInquiryById(inquiryId);
        if (existingInquiry.getResponse() != null) {
            throw new IllegalStateException("이미 답변이 등록된 문의사항입니다.");
        }

        // 답변 등록 시 상태를 '처리중'으로 변경
        CommonCode inProgressStatus = commonCodeRepository.findByCodeValue("PROCESSING");
        if (inProgressStatus == null) {
            throw new RuntimeException("처리중 상태 코드를 찾을 수 없습니다.");
        }

        // 현재 로그인한 교직원 정보
        Member currentMember = authService.getCurrentMember();
        
        // 답변 정보 설정
        response.setMember(currentMember);
        response.setCreatedAt(LocalDateTime.now());
        response.setRelatedInquiry(existingInquiry);  // 답변과 원본 문의 연결
        
        // 답변을 먼저 저장
        response = inquiryRepository.save(response);
        
        // 원본 문의사항 상태 업데이트
        existingInquiry.setInquiryStatus(inProgressStatus);
        existingInquiry.setUpdatedAt(LocalDateTime.now());
        
        return inquiryRepository.save(existingInquiry);
    }
}

