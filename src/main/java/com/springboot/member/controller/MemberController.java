package com.springboot.member.controller;

import com.springboot.auth.service.AuthService;
import com.springboot.email.service.EmailService;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.repository.EmployeeIdRepository;
import com.springboot.member.service.MemberService;
import com.springboot.response.SingleResponseDto;
import com.springboot.utils.UriCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;

@RestController
@Slf4j
@Validated
@RequestMapping("/members")
public class MemberController {

    private final static String MEMBER_DEFAULT_URL = "/members";
    private final MemberService memberService;
    private final MemberMapper mapper;
    private final AuthService authService;
    private final EmailService emailService;
    private final EmployeeIdRepository employeeIdRepository;

    public MemberController(MemberService memberService, MemberMapper mapper, AuthService authService, EmailService emailService, EmployeeIdRepository employeeIdRepository) {
        this.memberService = memberService;
        this.mapper = mapper;
        this.authService = authService;
        this.emailService = emailService;
        this.employeeIdRepository = employeeIdRepository;
    }

    @PostMapping
    public ResponseEntity postMember(@Valid @RequestBody MemberDto.Post requestBody) {
        // 사번 존재 여부 확인
        boolean isEmployeeIdExists = employeeIdRepository.existsByEmployeeId(requestBody.getEmployeeId());

        if (!isEmployeeIdExists) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }

        // Member 객체 생성
//        Member member = new Member();
//        member.setEmail(requestBody.getEmail());
//        member.setEmployeeId(requestBody.getEmployeeId().getEmployeeId());
//
//        // 멤버 생성 및 저장
//        Member createdMember = memberService.createMember(member);
//
//        // 응답 URI 생성
//        URI location = UriCreator.createUri(MEMBER_DEFAULT_URL, createdMember.getMemberId());
//        return ResponseEntity.created(location).build();
    }

    @GetMapping("/myInfo")
    public ResponseEntity getMember(
            Authentication authentication) {

        String email = (String) authentication.getPrincipal();
        Member member = memberService.findVerifiedMember(email);

        if (!member.getEmail().equals(email)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 이메일 불일치 시 권한 없음 상태 반환
        }

        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.memberToMemberResponseMyPage(member)), HttpStatus.OK);
    }

    @PatchMapping("/{member-id}")
    public ResponseEntity patchMember(@PathVariable("member-id") @Positive long memberId, @Valid @RequestBody MemberDto.Patch requestBody,
                                      Authentication authentication) {
        requestBody.setMemberId(memberId);
        String email = authentication.getName();

        Member member = memberService.updateMember(mapper.memberPatchToMember(requestBody), email);

        return new ResponseEntity<>(new SingleResponseDto<>(mapper.memberToMemberResponse(member)), HttpStatus.OK);

    }

    @GetMapping("/check-email")
    public ResponseEntity checkEmailDuplicate(@RequestBody MemberDto.EmailCheckDto requestBody) {
// 이메일 중복여부
        boolean isDuplicate = memberService.isEmailDuplicate(requestBody.getEmail());

        MemberDto.Check responseDto = new MemberDto.Check(isDuplicate);

        return new ResponseEntity<>(
                new SingleResponseDto<>(responseDto), HttpStatus.OK);
    }

    @GetMapping("/member-email")
    public ResponseEntity getMemberEmail(Authentication authentication) {

        String email = authentication.getName();

        Member member = memberService.findVerifiedMember(email);
        if (member == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 회원을 찾을 수 없을 때
        }

        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.memberToMemberResponse(member)), HttpStatus.OK);
    }

    @PatchMapping("/{member-id}/password")
    public ResponseEntity patchMemberPassword(@PathVariable("member-id") @Positive long memberId, @Valid @RequestBody MemberDto.PatchPassword requestBody,
                                              Authentication authentication) {
        requestBody.setMemberId(memberId);
        String email = authentication.getName();
        memberService.verifyPassword(memberId, requestBody.getPassword(), requestBody.getNewPassword());
        Member member = memberService.updatePassword(mapper.memberPatchPasswordToMember(requestBody), email);
        return  new ResponseEntity<>(
                new SingleResponseDto<>(mapper.memberToMemberResponse(member)), HttpStatus.OK);

    }

    @GetMapping("/check-employee/{employeeId}")
    public ResponseEntity checkEmployeeId(@PathVariable String employeeId) {
        boolean exists = memberService.existsByEmployeeId(employeeId);

        if (!exists) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
