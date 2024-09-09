package com.springboot.buyer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

public class Dto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class BuyerPostDto {
        @NotBlank(message = "구매자 코드는 필수 입력 항목입니다.")
        private String buyerCd;

        @NotBlank(message = "구매자 이름은 필수 입력 항목입니다.")
        private String buyerNm;

        @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
        private String tel;

        private String address;

        @NotBlank(message = "사업 유형은 필수 입력 항목입니다.")
        private String businessType;

        @Email(message = "유효한 이메일 형식을 입력해야 합니다.")
        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class BuyerPatchDto {
        private long buyerId;

        private String buyerNm;

        private String tel;

        private String address;

        private String businessType;

        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuyerResponse {
        private long buyerId;

        private String buyerCd;

        private String buyerNm;

        private String tel;

        private String address;

        private String businessType;

        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuyerResponseWithItemDto {
        private String buyerNm;
        private String buyerCd;
        private String tel;
        private List<com.springboot.buyer_item.dto.Dto.BuyerItemResponseDto> buyerItems;
    }
}
