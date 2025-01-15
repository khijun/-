package edu.du.campusflow.enums;

import edu.du.campusflow.annotation.Code;



@Code(name="처리 상태", description ="현재 상태")
public enum InquiryStatus {
    @Code(name ="대기", description = "대기상태")
    AWAITING,

    @Code(name = "처리중", description =  "처리중 상태")
    PROCESSING,

    @Code(name = "완료", description = "완료 상태")
    PROCESSED;


}
