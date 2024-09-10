package com.springboot.member.service;

import com.springboot.auth.utils.JwtAuthorityUtils;
import com.springboot.email.service.EmailService;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.helper.event.MemberRegistrationApplicationEvent;
import com.springboot.member.entity.EmployeeId;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.EmployeeIdRepository;
import com.springboot.member.repository.MemberRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.springboot.member.entity.Member.MemberStatus.MEMBER_ACTIVE;
import static com.springboot.member.entity.Member.MemberStatus.MEMBER_QUIT;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final EmployeeIdRepository employeeIdRepository;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthorityUtils authorityUtils;
    private final EmailService emailService;




    public MemberService(MemberRepository memberRepository, EmployeeIdRepository employeeIdRepository, ApplicationEventPublisher publisher, PasswordEncoder passwordEncoder, JwtAuthorityUtils authorityUtils, EmailService emailService) {
        this.memberRepository = memberRepository;
        this.employeeIdRepository = employeeIdRepository;
        this.publisher = publisher;
        this.passwordEncoder = passwordEncoder;
        this.authorityUtils = authorityUtils;
        this.emailService = emailService;
    }

    public Member createMember(Member member) {
        if (isTeamLeader()) {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED); // 권한 없음 예외 처리
            }
        }

        String randomPassword = generateRandomPassword();
        member.setPassword(passwordEncoder.encode(randomPassword)); // 비밀번호 암호화 후 저장
        List<String> roles = authorityUtils.createRoles(member.getEmail());
        member.setRoles(roles);


        Member savedMember = memberRepository.save(member);


        emailService.sendPasswordEmail(member.getEmail(), randomPassword);


        publisher.publishEvent(new MemberRegistrationApplicationEvent(this, savedMember));

        return savedMember;
    }

    public Member findMember(long memberId) {

        return findVerifiedMember(memberId);
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
    public Member findVerifiedEmployee(String employeeId){
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
            throw new BusinessLogicException(ExceptionCode.CONFIRM_PASSWORD_MISMATCH);
        }
    }

    public Member updatePassword(Member member, String email) {
        Member findMember = findVerifiedMember(member.getMemberId());

        if (member.getPassword() == null || member.getPassword().isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.PASSWORD_WRONG);
        }

        Optional.ofNullable(member.getPassword())
                .ifPresent(password -> findMember.setPassword(passwordEncoder.encode(member.getPassword())));

        return memberRepository.save(findMember);
    }
    public boolean existsByEmployeeId(String employeeId) {
        return memberRepository.existsByEmployeeId(employeeId);
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8); // 8자리 비밀번호 생성
    }

    public boolean isTeamLeader() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
