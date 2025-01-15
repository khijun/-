package edu.du.campusflow.repository;

import edu.du.campusflow.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Notice> findBySubjectContainingOrderByCreatedAtDesc(String keyword, Pageable pageable);
    Page<Notice> findByMember_NameContainingOrderByCreatedAtDesc(String keyword, Pageable pageable);
}
