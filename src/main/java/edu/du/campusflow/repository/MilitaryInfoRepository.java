package edu.du.campusflow.repository;


import edu.du.campusflow.entity.Member;
import edu.du.campusflow.entity.MilitaryInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MilitaryInfoRepository extends JpaRepository<MilitaryInfo,Long> {

}