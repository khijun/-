package edu.du.campusflow;

import edu.du.campusflow.entity.*;
import edu.du.campusflow.repository.*;
import edu.du.campusflow.service.ChangeRequestService;
import edu.du.campusflow.service.InfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
public class HjinTest {


    @Autowired
    private ChangeRequestRepository changeRequestRepository;

    @Autowired
    private ChangeHistoryRepository changeHistoryRepository;

    @Autowired
    private CommonCodeRepository commonCodeRepository;


    private Member member;
    private CommonCode beforeCode;
    private CommonCode afterCode;
    private CommonCode academicStatus;
    private CommonCode applicationStatus;
    @Autowired
    private ChangeRequestService changeRequestService;
    @Autowired
    private EducationInfoRepository educationInfoRepository;

    @Autowired
    private InfoService infoService;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void Test_add(){

        EducationInfo educationInfo = infoService.add_educationInfo(
                EducationInfo.builder()
                        .member(memberRepository.findByMemberId(202103001L))
                        .schoolName("혁진고등학교")
                        .enrollmentDate(LocalDateTime.now())
                        .graduationDate(LocalDateTime.now())
                        .graduationStatus(commonCodeRepository.findById(3L))
                        .build()
        )
    }





}
