package com.springboot.manufacture_history.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
public class ManufactureHistoryResponseDto {

    private Long mfHistoryId;
    private Long mfItemId;
    private Long mfId;
    private String mfCd;
    private String author;
    private LocalDateTime receiveDate;
    private String itemCd;
    private BigDecimal unitPrice;
    private int qty;
    private LocalDateTime createdAt;
}
