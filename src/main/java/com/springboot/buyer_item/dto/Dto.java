package com.springboot.buyer_item.dto;

import com.springboot.buyer_item.entity.BuyerItem;
import com.springboot.item.entity.Item;
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
        private long buyerId;

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
        private Item.ItemStatus itemStatus;
    }
}
