package com.springboot.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LoginDto {
    private String employeeId;
    private String password;


}
