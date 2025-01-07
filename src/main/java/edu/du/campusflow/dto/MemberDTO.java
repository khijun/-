package edu.du.campusflow.dto;

import edu.du.campusflow.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {
    private Long memberId;
    private String name;
    private String deptName;
    private String memberTypeStr;

    public static MemberDTO fromEntity(Member member) {
        if(member == null) return null;
        return MemberDTO.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .deptName(member.getDept().getDeptName())
                .memberTypeStr(member.getMemberType().getCodeName())
                .build();
    }
}