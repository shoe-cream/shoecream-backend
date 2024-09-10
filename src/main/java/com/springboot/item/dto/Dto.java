package com.springboot.item.dto;

import com.springboot.item.entity.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Dto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ItemPostDto {
        @NotBlank(message = "아이템 코드는 필수입니다.")
        private String itemCd;

        @NotBlank(message = "아이템 이름은 필수입니다.")
        private String itemNm;

        @NotBlank(message = "아이템의 단위는 필수입니다.")
        private String unit;

        private BigDecimal unitPrice;

        private int size;

        @NotBlank(message = "아이템의 색상은 필수입니다.")
        private String color;

        @NotBlank(message = "아이템의 카테코리는 필수입니다.")
        private String category;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ItemPatchDto {
        private String itemNm;
        private String unit;
        private BigDecimal unitPrice;
        private Item.ItemStatus itemStatus;
        private LocalDateTime modifiedAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ItemResponseDto {
        private long itemId;
        private String itemCd;
        private String itemNm;
        private String category;
        private String unit;
        private BigDecimal unitPrice;
        private String color;
        private int size;
        private Item.ItemStatus itemStatus;
        private LocalDateTime createdAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ItemDeleteRequestDto {
        private List<Long> itemId;

    }


}
