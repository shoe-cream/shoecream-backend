package com.springboot.buyer_item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springboot.buyer_item.entity.BuyerItem;
import com.springboot.item.entity.Item;
import lombok.*;

import javax.persistence.PrePersist;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Dto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class BuyerItemPostDto {
        @NotNull
        private String buyerNm;

        @NotNull
        private String itemNm;

        @DecimalMin(value = "0.01", message = "단가는 0보다 커야 합니다.")
        private BigDecimal unitPrice;

        private LocalDateTime startDate;

        private LocalDateTime endDate;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class BuyerItemPatchDto {
        @NotNull
        private Long buyerItemId;

        private BigDecimal unitPrice;

        private LocalDateTime startDate;

        private LocalDateTime endDate;

        private LocalDateTime modifiedAt;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BuyerItemResponseDto {
        private Long buyerItemId;
        private String buyerNm;     // 바이어 이름
        private String itemCd;      // 기본 아이템 코드
        private String itemNm;      // 기본 아이템 이름
        private String unit;        // 기본 단위
        private BigDecimal unitPrice; // 바이어 맞춤 단가
        private LocalDateTime startDate; // 계약 시작기간
        private LocalDateTime endDate;   // 계약 종료기간
        private LocalDateTime modifiedAt;
        private Item.ItemStatus itemStatus;  // 아이템 상태
    }

    @Getter
    public static class BuyerItemDeleteDtos {
        private List<Long> itemId;
    }
}
