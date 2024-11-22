package com.springboot.buyer.dto;

import com.springboot.buyer.entity.Buyer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

public class Dto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class BuyerPostDto {

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
        @NotNull
        private Long buyerId;

        private String buyerNm;

        private Buyer.BuyerStatus buyerStatus;

        private String tel;

        private String address;

        private String businessType;

        @Email(message = "유효한 이메일 형식을 입력해야 합니다.")
        private String email;

        private LocalDateTime modifiedAt;
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

        private Buyer.BuyerStatus buyerStatus;

        private String email;
    }

    @Getter
    public static class BuyerDeleteDtos {
        @NotNull(message = "buyerId의 List는 null일 수 없습니다.")
        @NotEmpty(message = "buyerId의 List는 비어있을 수 없습니다.")
        @Size(min = 1, message = "buyerId의 List 최소 1개 이상이어야 합니다.")
        private List<Long> buyerId;
    }
}
