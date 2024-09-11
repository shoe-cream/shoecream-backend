package com.springboot.email.controller;


import com.springboot.email.dto.EmailRequestDto;
import com.springboot.email.service.EmailService;
import com.springboot.member.service.MemberService;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;

@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService, MemberService memberService) {
        this.emailService = emailService;
    }


    @PostMapping
    public String sendVerificationEmail(@RequestBody EmailRequestDto requestDto) throws MessagingException, IOException {
        emailService.sendEmail(requestDto.getEmail());
        return "Verification email sent.";
    }



}
