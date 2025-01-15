package edu.du.campusflow.repository;

import edu.du.campusflow.entity.Inquiry;
import edu.du.campusflow.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    // 모든 원본 문의사항 조회 (생성일 기준 내림차순)
    List<Inquiry> findByRelatedInquiryIsNullOrderByCreatedAtDesc();
    
    // 특정 사용자의 원본 문의사항 조회 (생성일 기준 내림차순)
    List<Inquiry> findByMemberAndRelatedInquiryIsNullOrderByCreatedAtDesc(Member member);
}