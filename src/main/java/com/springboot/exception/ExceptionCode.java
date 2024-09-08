package com.springboot.exception;

import lombok.Getter;

public enum ExceptionCode {
    PASSWORD_MISMATCH(409,"Password Not Changed"),
    PASSWORD_WRONG(400, "Password Wrong"),
    CONFIRM_PASSWORD_MISMATCH(400,"Passwords Do Not Match"),
    ACCESS_DENIED(403,"Access Denied"),
    MEMBER_NOT_FOUND(404,"Member Not Found"),
    EMAIL_ALREADY_EXIST(409, "Email Already Exist"),
    BUYER_ALREADY_EXIST(409, "BuyerCd Already Exist"),
    BUYER_NOT_FOUND(409, "Buyer Not Found"),
    CONDITION_NOT_FIT(404, "해당 조건에 맞는 바이어는 없습니다."),
    AT_LEAST_ONE_CONDITION(404, "적어도 하나의 검색 조건을 입력해야 합니다."),
    ORDER_NOT_FOUND(404,"Order Not Found"),
    ITEM_NOT_FOUND(404,"Item Not Found" ),
    MEMBER_EXISTS(409,"Member Already Exists" ),
    EMAIL_NOT_AUTH(409, "Email not Auth"),
    CANNOT_CHANGE_MEMBER_STATUS(403, "MemberStatus cannot change" ),
    CONFIRM_TYPE(400, "Please Confirm Type" );

    @Getter
    private int status;
    @Getter
    private String message;

    ExceptionCode (int status, String message) {
        this.status = status;
        this.message = message;
    }
}
