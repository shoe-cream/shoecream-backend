package com.springboot.email.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class EmailCheckDto {
    @Email
    @NotEmpty
    private String email;
}
