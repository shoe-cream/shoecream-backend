package com.springboot.member.service;

import com.springboot.auth.utils.JwtAuthorityUtils;
import com.springboot.email.service.EmailService;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

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


    public Member findMember(long memberId) {

        return findVerifiedMember(memberId);
    }

    public Member updateMember(Member member, String employeeId) {
        Member findMember = findVerifiedEmployee(employeeId);
        Optional.ofNullable(member.getName())
                .ifPresent(name -> findMember.setName(name));
        Optional.ofNullable(member.getTel())
                .ifPresent(tel -> findMember.setTel(tel));
        Optional.ofNullable(member.getAddress())
                .ifPresent(address -> findMember.setAddress(address));
        Optional.ofNullable(member.getMemberStatus())
                .ifPresent(findMember::setMemberStatus);

        return memberRepository.save(findMember);
    }


    public boolean isEmailDuplicate(String email) {
        return !memberRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public Member findVerifiedMember(String email) {
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
    public Member findVerifiedEmployee(String employeeId) {
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

    public void verifyPassword(long memberId, String password, String newPassword) {
        Member member = findVerifiedMember(memberId);
        if (!passwordEncoder.matches(password, member.getPassword())) {
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
    public Member uploadProfile(String email, String profileUrl) {
        Member member = findVerifiedMember(email);
        member.setProfileUrl(profileUrl);
        return memberRepository.save(member);
    }

    // 프로필 사진 수정
    public Member updateProfile(String employeeId, String newProfileUrl) {
        Member member = findVerifiedEmployee(employeeId);
        member.setProfileUrl(newProfileUrl);
        return memberRepository.save(member);
    }

    // 프로필 사진 삭제 (기본 이미지로 변경)
    public Member deleteProfile(String email) {
        Member member = findVerifiedMember(email);
        member.setProfileUrl("https://img.hankyung.com/photo/202208/BF.30820179.1.jpg"); // 기본 이미지 URL
        return memberRepository.save(member);
    }

//    public Member updateRole(long memberId, String newRole) {
//        Member member = findVerifiedMember(memberId);
//
//        // 새로운 역할 설정 (기존 역할 리스트에 추가 or 덮어쓰기)
//        member.getRoles().clear();  // 기존 역할 제거 (덮어쓰는 경우)
//        member.getRoles().add(newRole);
//
//        return memberRepository.save(member);
//    }

}
