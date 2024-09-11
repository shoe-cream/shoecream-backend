package com.springboot.exception;

import lombok.Getter;

public enum ExceptionCode {
    PASSWORD_MISMATCH(409,"Password Not Changed"),
    PASSWORD_WRONG(400, "Password Wrong"),
    CONFIRM_PASSWORD_MISMATCH(400,"Passwords Do Not Match"),
    ACCESS_DENIED(403,"Access Denied"),
    MEMBER_NOT_FOUND(404,"Member Not Found"),
    EMAIL_ALREADY_EXISTS(409, "Email Already Exist"),
    BUYER_ALREADY_EXIST(409, "BuyerCd Already Exist"),
    BUYER_NOT_FOUND(409, "Buyer Not Found"),
    CONDITION_NOT_FIT(404, "해당 조건에 맞는 바이어는 없습니다."),
    AT_LEAST_ONE_CONDITION(404, "적어도 하나의 검색 조건을 입력해야 합니다."),
    BUYERITEM_NOT_FOUND(409, "바이어아이템을 찾을수 없습니다."),
    MANUFACTURE_NOT_FOUND(409, "제조사를 찾을수 없습니다"),
    MANUFACTURE_CODE_EXIST(409, "MF Code Already Exists"),
    ORDER_NOT_FOUND(404,"Order Not Found"),
    ITEM_NOT_FOUND(404,"Item Not Found" ),
    MEMBER_EXISTS(409,"Member Already Exists" ),
    EMAIL_NOT_AUTH(409, "Email not Auth"),
    CANNOT_CHANGE_MEMBER_STATUS(403, "MemberStatus cannot change" ),
    CANNOT_CHANGE_ORDER_STATUS(403,"not change status" ),
    ITEM_NOT_FOUND_IN_ORDER(404, "The item does not belong to order"),
    OUT_OF_STOCK(409, "The item is out of stock" ),
    ITEM_CD_ALREADY_EXISTS(409, "Item Code Already Exists" ),
    ITEM_NAME_ALREADY_EXISTS(409,"Item Name Already Exists" ),
    MANUFACTURE_NAME_EXIST(409,"MF Name Already Exists" ),
    INVALID_SORT_FIELD(400,"Invalid Sort Field" );

    @Getter
    private int status;
    @Getter
    private String message;

    ExceptionCode (int status, String message) {
        this.status = status;
        this.message = message;
    }
}
