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
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;
    private final MemberService memberService;

    public EmailController(EmailService emailService, MemberService memberService) {
        this.emailService = emailService;
        this.memberService = memberService;
    }

    @PostMapping("/send-verification")
    public String sendVerificationEmail(@RequestBody EmailRequestDto requestDto) {
        memberService.verifyExistsEmail(requestDto.getEmail());
        emailService.sendVerificationEmail(requestDto.getEmail());
        return "Verification email sent.";
    }

    @PostMapping("/verify")
    public ResponseEntity verifyAuthCode(@RequestBody EmailAuthDto request) {
        boolean isValid = emailService.verifyAuthCode(request.getEmail(), request.getCode());
        if (isValid) {
            return ResponseEntity.ok("인증번호 검증 성공");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증번호 불일치");
        }
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
