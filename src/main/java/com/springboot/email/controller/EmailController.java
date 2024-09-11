package com.springboot.email.controller;


import com.springboot.email.dto.EmailRequestDto;
import com.springboot.email.service.EmailService;
import com.springboot.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;

@RestController
@RequestMapping("/email")
public class EmailController {


    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmailWithAttachment(
            @Validated @ModelAttribute EmailRequestDto emailRequestDto,  // DTO를 통해 이메일과 내용을 받음
            @RequestParam("file") MultipartFile file) throws MessagingException, IOException {

        byte[] fileBytes = file.getBytes();
        String fileName = file.getOriginalFilename();

        // 이메일과 내용을 받아서 이메일 전송 로직 실행
        emailService.sendEmailWithAttachment(emailRequestDto.getEmail(), fileBytes, fileName);

        return ResponseEntity.ok("파일이 포함된 이메일이 성공적으로 발송되었습니다.");
    }



}
