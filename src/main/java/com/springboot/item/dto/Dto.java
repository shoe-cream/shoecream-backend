package com.springboot.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

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

        @NotBlank(message = "아이템의 단가는 필수입니다.")
        private BigDecimal unitPrice;

        private int size;

        @NotBlank(message = "아이템의 색상은 필수입니다.")
        private String color;

        @NotBlank(message = "아이템의 카테코리는 필수입니다.")
        private String category;
    }
}
