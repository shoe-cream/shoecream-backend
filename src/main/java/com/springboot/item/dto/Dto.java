package com.springboot.item.dto;

import com.springboot.item.entity.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    @Builder
    public static class ItemPatchDto {
        @NotNull(message = "ItemId는 필수입니다.")
        private Long itemId;
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
        private Integer totalStock; // 총 재고량
        private Integer prepareOrder; // 주문대기 수량
        private Integer unusedStock;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ItemDeleteRequestDto {
        @NotNull(message = "itemId의 List는 null일 수 없습니다.")
        @NotEmpty(message = "itemId의 List는 비어있으면 안됩니다.")
        @Size(min = 1, message = "itemId의 List는 최소 1개 이상이어야 합니다.")
        private List<Long> itemId;

    }


}
