package com.springboot.email.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendEmailWithAttachment(String to, byte[] fileBytes, String fileName) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // 받는 사람, 제목, 발신자 설정
        helper.setTo(to);
        helper.setSubject("파일 첨부 이메일");
        helper.setFrom("your-email@gmail.com");

        // Thymeleaf 컨텍스트 설정
        Context context = new Context();
        context.setVariable("name", "User");

        // Thymeleaf 템플릿에서 본문 구성
        String htmlContent = templateEngine.process("emailTemplate", context);
        helper.setText(htmlContent, true); // HTML로 본문 설정

        // 파일 첨부
        helper.addAttachment(fileName, new ByteArrayResource(fileBytes));

        // 이메일 발송
        mailSender.send(message);
    }
}
