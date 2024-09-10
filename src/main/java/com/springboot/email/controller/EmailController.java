package com.springboot.email.controller;


import com.springboot.email.dto.EmailAuthDto;
import com.springboot.email.dto.EmailCheckDto;
import com.springboot.email.dto.EmailRequestDto;
import com.springboot.email.service.EmailService;
import com.springboot.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;
    private final MemberService memberService;
    private final String bypassCode = "1234";

    public EmailController(EmailService emailService, MemberService memberService) {
        this.emailService = emailService;
        this.memberService = memberService;
    }


    @PostMapping("/validate-email")
    public String validateEmail(@RequestBody EmailCheckDto emailDto) {
        String email = emailDto.getEmail();
        // 이메일 유효성 검사 로직 수행
        return "Received email: " + email;
    }

    @PostMapping("/send-password-reset")
    public String sendPasswordResetEmail(@RequestParam String email) {

        String resetToken = java.util.UUID.randomUUID().toString();

        emailService.sendPasswordResetEmail(email, resetToken);
        return "Password reset email sent.";
    }
}
