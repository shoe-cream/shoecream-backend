package com.springboot.member.controller;

import com.springboot.auth.service.AuthService;
import com.springboot.email.service.EmailService;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import com.springboot.response.SingleResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.GrantedAuthority;


import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@Validated
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final MemberMapper mapper;
    private final AuthService authService;
    private final EmailService emailService;

    public MemberController(MemberService memberService, MemberMapper mapper, AuthService authService, EmailService emailService) {
        this.memberService = memberService;
        this.mapper = mapper;
        this.authService = authService;
        this.emailService = emailService;
    }

    // 현재 로그인된 사용자의 정보를 조회
    @GetMapping("/my-info")
    public ResponseEntity<?> getMember(Authentication authentication) {
        String employeeId = (String) authentication.getPrincipal();
        Member member = memberService.findVerifiedEmployee(employeeId);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (!member.getEmployeeId().equals(employeeId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        MemberDto.Response response = mapper.memberToMemberResponseMyPage(member, roles);

        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }

    // 멤버 정보 업데이트
    @PatchMapping("/{member-id}")
    public ResponseEntity<?> patchMember(@PathVariable("member-id") @Positive long memberId, @Valid @RequestBody MemberDto.Patch requestBody,
                                         Authentication authentication) {
        requestBody.setMemberId(memberId);
        String employeeId = authentication.getName();

        Member member = memberService.updateMember(mapper.memberPatchToMember(requestBody), employeeId);
        return new ResponseEntity<>(new SingleResponseDto<>(mapper.memberToMemberResponse(member)), HttpStatus.OK);
    }

    // 이메일 중복 여부 체크
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailDuplicate(@RequestBody MemberDto.EmailCheckDto requestBody) {
        boolean isDuplicate = memberService.isEmailDuplicate(requestBody.getEmail());
        String message = isDuplicate ? "emailExist" : "emailAvailable";
        return new ResponseEntity<>(new SingleResponseDto<>(new MemberDto.Check(isDuplicate, message)), HttpStatus.OK);
    }

    // 로그인된 사용자의 사번으로 멤버 정보 조회
    @GetMapping("/member-employeeId")
    public ResponseEntity<?> getMemberByEmployeeId(Authentication authentication) {
        String employeeId = authentication.getName();
        Member member = memberService.findVerifiedEmployee(employeeId);

        return new ResponseEntity<>(new SingleResponseDto<>(mapper.memberToMemberResponse(member)), HttpStatus.OK);
    }

    // 비밀번호 변경
    @PatchMapping("/{member-id}/password")
    public ResponseEntity<?> patchMemberPassword(@PathVariable("member-id") @Positive long memberId, @Valid @RequestBody MemberDto.PatchPassword requestBody,
                                                 Authentication authentication) {
        requestBody.setMemberId(memberId);
        String employeeId = authentication.getName();

        // 비밀번호 검증 및 변경 처리
        memberService.verifyPassword(memberId, requestBody.getPassword(), requestBody.getNewPassword());
        Member member = memberService.updatePassword(mapper.memberPatchPasswordToMember(requestBody), employeeId);
        return new ResponseEntity<>(new SingleResponseDto<>(mapper.memberToMemberResponse(member)), HttpStatus.OK);
    }

    // 직원 ID 중복 여부 체크
    @GetMapping("/check-employee/{employeeId}")
    public ResponseEntity<?> checkEmployeeId(@PathVariable String employeeId) {
        boolean exists = memberService.existsByEmployeeId(employeeId);

        if (!exists) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 프로필 사진 업로드
    @PostMapping("/profile")
    public ResponseEntity<?> uploadProfile(@Valid @RequestBody MemberDto.UploadProfile profileUploadDto, Authentication authentication) {
        String employeeId = authentication.getName();
        Member updatedMember = memberService.uploadProfile(employeeId, profileUploadDto.getProfileUrl());
        return new ResponseEntity<>(new SingleResponseDto<>(updatedMember), HttpStatus.OK);
    }

    // 프로필 사진 수정
    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody MemberDto.Update profileUpdateDto, Authentication authentication) {
        String employeeId = (String) authentication.getName();
        Member updatedMember = memberService.updateProfile(employeeId, profileUpdateDto.getProfileUrl());
        return new ResponseEntity<>(new SingleResponseDto<>(updatedMember), HttpStatus.OK);
    }

    // 프로필 사진 삭제
    @DeleteMapping("/profile")
    public ResponseEntity<?> deleteProfile(Authentication authentication) {
        String employeeId = (String) authentication.getName();
        memberService.deleteProfile(employeeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // 삭제 성공 시 204 응답 반환
    }

}

