package com.springboot.email.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.Duration;
import java.util.Random;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SpringTemplateEngine templateEngine;

    private MemberRepository memberRepository;

    private static final String EMAIL_PREFIX = "email:";
    private static final String RESET_PREFIX = "reset:";

    public void sendVerificationEmail(String email) {
        String authCode = generateAuthCode();
        redisTemplate.opsForValue().set(EMAIL_PREFIX + email, authCode, Duration.ofMinutes(10)); // 10분 동안 유효

        String subject = "회원 가입 인증 이메일입니다.";
        Context context = new Context();
        context.setVariable("code", authCode);
        String content = templateEngine.process("verificationEmail", context);
        sendEmail(email, subject, content);
    }

    private String generateAuthCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 숫자
        return String.valueOf(code);
    }

    private void sendEmail(String toEmail, String subject, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("your-email@example.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true); // HTML 형식으로 설정
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    public void sendPasswordResetEmail(String email, String resetToken) {
        redisTemplate.opsForValue().set(RESET_PREFIX + resetToken, email, Duration.ofHours(1)); // 1시간 동안 유효
        if (memberRepository.existsByEmail(email)) {
            String resetLink = "http:// ubuntu@ec2-3-36-67-129.ap-northeast-2.compute.amazonaws.com:8080/reset-password?token=" + resetToken;
            String subject = "비밀번호 재설정 링크입니다.";
            String content = "비밀번호 재설정을 하려면 다음 링크를 클릭하세요: <a href=\"" + resetLink + "\">비밀번호 재설정</a>";

            sendEmail(email, subject, content);
        } else {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }
    }

    public boolean verifyAuthCode(String email, String authCode) {
        String storedCode = redisTemplate.opsForValue().get(EMAIL_PREFIX + email);
        return authCode.equals(storedCode);
    }

    public boolean verifyFinalAuthCode(String email, String authCode) {
        String storedCode = redisTemplate.opsForValue().get(EMAIL_PREFIX + email);
        redisTemplate.delete(EMAIL_PREFIX + email);
        return authCode.equals(storedCode);
    }

    public String getEmailByResetToken(String resetToken) {
        return redisTemplate.opsForValue().get(RESET_PREFIX + resetToken);
    }
    public void sendEmailWithImage(String to, String subject, String body) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true); // true: 멀티파트 메시지 사용
        helper.setTo(to);
        helper.setSubject(subject);

        // 이메일 본문을 HTML로 설정
        helper.setText(body, true);

        // 이미지 파일을 첨부
        ClassPathResource image = new ClassPathResource("https://github.com/user-attachments/assets/0a582994-ddb8-4269-875b-ac7c2c60b193");
        helper.addInline("shoeCreamImage", image);

        // 이메일 발송
        mailSender.send(message);
    }


    public void sendEmail(String email) throws MessagingException, IOException {
        String to = email;
        String subject = "ShoeCream - 이미지 포함 이메일";
        String body = "<html><body><h1>이미지 포함</h1><img src='cid:shoeCreamImage'></body></html>";
        sendEmailWithImage(to, subject, body);
    }
}
