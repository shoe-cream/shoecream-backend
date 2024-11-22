package com.springboot.report.reportDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

public class ReportDto {
    @Getter
    @Builder
    public static class InventoryDto {
        private Long itemId;
        private String itemName;
        private Integer totalStock; // 총 재고
        private Integer unusedStock; // 불용재고 (불합격)
        private Integer preparedOrder; // 주문대기 수량
        private Integer totalOrder; // 총 주문량
        private Integer totalSupply; //총 공급량
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SaleReportDto {
        private String itemCd;
        private String itemNm;
        private int totalManufactured; //제품에 대한 총 공급량
        private int totalOrdered; //제품에 대한 총 주문량
        private BigDecimal totalOrderedPrice; //총 주문 가격
        private BigDecimal totalMfPrice; //총 공급가격
        private BigDecimal marginRate; // 마진률
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class EmployeeReportDto {
        private String employeeId;
        private String employeeName;
        private BigDecimal marginRate; // 평균 마진률
        private int totalOrderCount; // 주문건수
        private BigDecimal totalOrderPrice; // 판매금액
    }
}
