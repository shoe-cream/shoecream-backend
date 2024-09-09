package com.springboot.buyer_item.dto;

import com.springboot.buyer_item.entity.BuyerItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Dto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class BuyerItemPostDto {
        @NotBlank(message = "아이템 코드는 필수입니다.")
        private String buyerNm;

        @NotBlank(message = "바이어 코드는 필수입니다.")
        private String buyerCd;  // 바이어 코드 추가

        @NotBlank(message = "아이템 이름은 필수입니다.")
        private String itemNm;

//        private String itemCd;

//        private String category;

        @NotBlank(message = "단위는 필수입니다.")
        private String unit;

        @DecimalMin(value = "0.01", message = "단가는 0보다 커야 합니다.")
        private BigDecimal unitPrice;

        private LocalDateTime startDate = LocalDateTime.now();

        private LocalDateTime endDate = LocalDateTime.now();

//        담장자
//        private String member;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class BuyerItemPatchDto {
        private long buyerItemId;

        private String buyerNm;

        private String itemNm;

        private String unit;

        private BigDecimal unitPrice;

        private LocalDateTime startDate = LocalDateTime.now();

        private LocalDateTime endDate = LocalDateTime.now();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class BuyerItemResponseDto {
        private String buyerNm;
        private String itemCd;      // 기본 아이템 코드
        private String itemNm;      // 기본 아이템 이름
        private String unit;        // 기본 단위
        private BigDecimal unitPrice; // 바이어 맞춤 단가
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private BuyerItem.ItemStatus itemStatus;
    }
}
