package com.springboot.helper.event;

import com.springboot.helper.email.EmailSender;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailSendException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;


@EnableAsync
@Configuration
@Component
@Slf4j
public class MemberRegistrationEventListener {
    private final EmailSender emailSender;
    private final MemberService memberService;

    public MemberRegistrationEventListener(EmailSender emailSender, MemberService memberService) {
        this.emailSender = emailSender;
        this.memberService = memberService;
    }
    @Async
    @EventListener
    public void listen(MemberRegistrationApplicationEvent event) throws  Exception{
        try {
            String message = "any email message";
            emailSender.sendEmail(message);
        }catch (MailSendException e){
            e.printStackTrace();
            log.error("MailSendException: rollback for Member Registration:");
            Member member = event.getMember();
            memberService.deleteMember(member.getEmail());
        }
    }
}
