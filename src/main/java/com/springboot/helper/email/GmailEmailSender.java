package com.springboot.helper.email;

import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Component;

@Component
public class GmailEmailSender implements EmailSendable {
    @Override
    public void send(String message) throws MailSendException, InterruptedException {
        // 실제 이메일 전송 로직을 여기에 작성합니다.
        // 예시로 단순하게 출력만 해볼 수 있습니다.
        System.out.println("Sending email with message: " + message);
        // 여기서 실제로 SMTP 서버를 사용해 이메일을 보내는 코드를 작성하면 됩니다.
    }

}
