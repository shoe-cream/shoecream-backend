package com.springboot.auth.controller;

import com.springboot.auth.jwt.JwtTokenizer;
import com.springboot.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final AuthService authService;
    private final JwtTokenizer jwtTokenizer;

    // 생성자 주입을 통해 AuthService와 JwtTokenizer를 주입받습니다.
    public AuthController(AuthService authService, JwtTokenizer jwtTokenizer) {
        this.authService = authService;
        this.jwtTokenizer = jwtTokenizer;
    }


    @PostMapping("/logout") // "/auth/logout" 경로로 POST 요청을 처리하는 메서드로 지정합니다.
    public ResponseEntity postLogout(Authentication authentication) {
        String username = authentication.getName(); // 현재 인증된 사용자의 사용자명을 가져옵니다.

        // AuthService의 logout 메서드를 호출하여 로그아웃을 처리하고, 결과에 따라 HTTP 상태 코드를 반환합니다.
        return authService.logout(username) ?
                new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
