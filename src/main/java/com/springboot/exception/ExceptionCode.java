package com.springboot.exception;

import lombok.Getter;

public enum ExceptionCode {
    PASSWORD_WRONG(400, "Password Wrong"),
    CONFIRM_PASSWORD_MISMATCH(400,"Passwords Do Not Match"),
    ACCESS_DENIED(403,"Access Denied"),
    MEMBER_NOT_FOUND(404,"Member Not Found"),
    BUYER_ALREADY_EXIST(409, "Buyer Already Exist"),
    BUYER_CD_ALREADY_EXIST(409, "BuyerCd Already Exist"),
    BUYER_NOT_FOUND(409, "Buyer Not Found"),
    BUYER_ITEM_NOT_FOUND(409, "바이어아이템을 찾을수 없습니다."),
    MANUFACTURE_NOT_FOUND(409, "제조사를 찾을수 없습니다"),
    MANUFACTURE_CODE_EXIST(409, "MF Code Already Exists"),
    ORDER_NOT_FOUND(404,"Order Not Found"),
    ITEM_NOT_FOUND(404,"Item Not Found" ),
    MEMBER_EXISTS(409,"Member Already Exists" ),
    CANNOT_CHANGE_MEMBER_STATUS(403, "MemberStatus cannot change" ),
    CANNOT_CHANGE_ORDER_STATUS(403,"not change status" ),
    ITEM_NOT_FOUND_IN_ORDER(404, "The item does not belong to order"),
    OUT_OF_STOCK(409, "The item is out of stock" ),
    ITEM_CD_ALREADY_EXISTS(409, "Item Code Already Exists" ),
    ITEM_NAME_ALREADY_EXISTS(409,"Item Name Already Exists" ),
    MANUFACTURE_NAME_EXIST(409,"MF Name Already Exists" ),
    INVALID_SORT_FIELD(400,"Invalid Sort Field" ),
    EMAIL_ALREADY_EXISTS(409,"Email Already Exists" ),
    ORDER_CD_NOT_FOUND(404,"Order Code Not Found" ),
    INVALID_REQUEST(400, "Invalid request" ),
    INACTIVE_STATUS(400, "비활성 상태입니다."),
    CHECK_REQUEST_DATE(400, "Check the request date" ),
    CHECK_CONTRACT_DATE(400, "Check the contract date"),
    CANNOT_ORDER_NEGATIVE_QUANTITY(400, "The order quantity must not be negative" ),
    PERIOD_OVERLAP_ERROR(409, "The specified period overlaps with an existing period" ),
    TEL_ALREADY_EXIST(409, "Tel Already Exists"),
    BUYER_NAME_ALREADY_EXIST(409, "Buyer Name Already Exists" );

    @Getter
    private int status;
    @Getter
    private String message;

    ExceptionCode (int status, String message) {
        this.status = status;
        this.message = message;
    }
}
