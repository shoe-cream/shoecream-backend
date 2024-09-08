package com.springboot.member.service;

import com.springboot.email.service.EmailService;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.helper.event.MemberRegistrationApplicationEvent;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.springboot.member.entity.Member.MemberStatus.MEMBER_ACTIVE;
import static com.springboot.member.entity.Member.MemberStatus.MEMBER_QUIT;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthorityUtils authorityUtils;
    private final EmailService emailService;



    public MemberService(MemberRepository memberRepository, ApplicationEventPublisher publisher, PasswordEncoder passwordEncoder, JwtAuthorityUtils authorityUtils, EmailService emailService) {
        this.memberRepository = memberRepository;
        this.publisher = publisher;
        this.passwordEncoder = passwordEncoder;
        this.authorityUtils = authorityUtils;
        this.emailService = emailService;
    }

    public Member createMember(Member member) {
        verifyExistsEmail(member.getEmail());

//        String encryptedPassword = passwordEncoder.encode(member.getPassword());
//        member.setPassword(encryptedPassword);
//
//        List<String> roles = authorityUtils.createRoles(member.getEmail());
//        member.setRoles(roles);


        Member savedMember = memberRepository.save(member);
        publisher.publishEvent(new MemberRegistrationApplicationEvent(this, savedMember));
        return savedMember;
    }

    public Member updateMember(Member member, String email) {
        Member findMember = findVerifiedMember(email);
        Optional.ofNullable(member.getName())
                .ifPresent(name -> findMember.setName(name));


        Optional.ofNullable(member.getMemberStatus())
                .ifPresent(memberStatus -> findMember.setMemberStatus(memberStatus));


        return memberRepository.save(findMember);
    }


    public boolean isEmailDuplicate(String email) {
        return !memberRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public Member findVerifiedMember(String email){
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        Member findMember = optionalMember.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return findMember;
    }
    public Member findVerifiedMember(long memberId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        return optionalMember.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }
    @Transactional(readOnly = true)
    public Member findVerifiedEmployee(long employeeId){
        Optional<Member> optionalMember = memberRepository.findByEmployeeId(employeeId);
        Member findMember = optionalMember.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return findMember;
    }

    public void verifyExistsEmail(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
    }

    public void deleteMember(String email) {
        Member findMember = findVerifiedMember(email);

        findMember.setMemberStatus(MEMBER_QUIT);

        memberRepository.save(findMember);
    }

    public void sleepMember(long memberId) {
        Member findMember = findVerifiedMember(memberId);

        if (findMember.getMemberStatus() != MEMBER_ACTIVE) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_CHANGE_MEMBER_STATUS);
        }

        findMember.setMemberStatus(Member.MemberStatus.MEMBER_SLEEP);
        memberRepository.save(findMember);
    }

    public void verifyPassword(long memberId, String password, String newPassword){
        Member member = findVerifiedMember(memberId);
        if(!passwordEncoder.matches(password, member.getPassword())){
            throw new BusinessLogicException(ExceptionCode.PASSWORD_WRONG);
        }
    }
}
