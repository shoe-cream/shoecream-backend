package com.springboot.item_manufacture.dto;

import lombok.*;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class Dto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ItemMfPostDto {
        @Min(value = 1, message = "Item ID는 1 이상이어야 합니다.")
        private long itemId;

        @Min(value = 1, message = "Manufacture ID는 1 이상이어야 합니다.")
        private long mfId;

        @DecimalMin(value = "0.01", message = "단가는 0보다 커야 합니다.")
        private BigDecimal unitPrice;

        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        private int qty;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemMfResponseDto {
        private String region; // 제조국
        private String email;  // 제조사 email
        private String mfNm;   // 공장명
        private String mfCd;   // 공장코드
        private String itemCd; // 아이템 코드
        private String itemNm; // 아이템 명
        private BigDecimal unitPrice; // 제조사 맞춤 단가
        private int qty;        // 제품 수량
    }
}
