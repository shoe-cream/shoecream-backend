package com.springboot.manufacture.dto;

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
        private Long mfId;
        private String region;
        private String email;
        private String mfNm;
        private String mfCd;
    }

    @Getter
    public static class ManufactureDeleteList {
        @NotNull(message = "mfId의 List는 null일 수 없습니다.")
        @NotEmpty(message = "mfId의 List는 비어있으면 안됩니다.")
        @Size(min = 1, message = "mfId의 List는 최소 1개 이상이어야 합니다.")
        private List<Long> mfId;
    }
}
