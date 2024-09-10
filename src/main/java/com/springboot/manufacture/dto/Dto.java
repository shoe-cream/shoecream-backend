package com.springboot.manufacture.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class Dto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ManufacturePostDto {
        @NotBlank
        private String region;

        @Email
        private String email;

        @NotBlank
        private String mfNm;

        @NotBlank
        private String mfCd;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ManufacturePatchDto {
        private long mfId;
        private String region;
        private String email;
        private String mfNm;
        private LocalDateTime modifiedAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ManufactureResponseDto {
        private String region;
        private String email;
        private String mfNm;
        private String mfCd;
    }
}
