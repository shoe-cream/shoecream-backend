package com.springboot.email.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class EmailAuthDto {
    @Email
    @NotBlank
    private String email;
    private String code;
}
