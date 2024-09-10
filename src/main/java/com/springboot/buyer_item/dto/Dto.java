package com.springboot.buyer_item.dto;

import com.springboot.buyer_item.entity.BuyerItem;
import com.springboot.item.entity.Item;
import lombok.*;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Dto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class BuyerItemPostDto {
        @Min(value = 1, message = "buyer ID는 1 이상이어야 합니다.")
        private long buyerId;

        @Min(value = 1, message = "item ID는 1 이상이어야 합니다.")
        private long itemId;

        @DecimalMin(value = "0.01", message = "단가는 0보다 커야 합니다.")
        private BigDecimal unitPrice;

        private LocalDateTime startDate;

        private LocalDateTime endDate;
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

        private LocalDateTime modifiedAt;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BuyerItemResponseDto {
        private String buyerNm;     // 바이어 이름
        private String itemCd;      // 기본 아이템 코드
        private String itemNm;      // 기본 아이템 이름
        private String unit;        // 기본 단위
        private BigDecimal unitPrice; // 바이어 맞춤 단가
        private LocalDateTime startDate; // 계약 시작기간
        private LocalDateTime endDate;   // 계약 종료기간
        private Item.ItemStatus itemStatus;  // 아이템 상태
    }
}
