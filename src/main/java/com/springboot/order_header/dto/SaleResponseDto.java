package com.springboot.order_header.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SaleResponseDto {
    @Getter
    @Builder
    public static class InventoryDto {
        private Long itemId;
        private String itemName;
        private Integer totalStock; // 총 재고
        private Integer totalOrder; // 총 주문량
        private Integer totalSupply; //총 공급량
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SaleReportDto {
        private String itemCd;
        private Integer totalManufactured; //제품에 대한 총 공급량
        private Integer totalOrdered; //제품에 대한 총 주문량
        private BigDecimal totalOrderedPrice; //총 주문 가격
        private BigDecimal totalMfPrice; //총 공급가격
        private BigDecimal marginRate; // 마진률
    }
}
