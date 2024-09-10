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

import javax.validation.Valid;
import javax.validation.constraints.Positive;

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

    public MemberController(MemberService memberService, MemberMapper mapper, AuthService authService, EmailService emailService) {
        this.memberService = memberService;
        this.mapper = mapper;
        this.authService = authService;
        this.emailService = emailService;
    }


    @GetMapping("/my-info")
    public ResponseEntity getMember(
            Authentication authentication) {

        String empolyeeId = (String) authentication.getPrincipal();
        Member member = memberService.findVerifiedEmployee(empolyeeId);

        if (!member.getEmployeeId().equals(empolyeeId)) {
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

    @PostMapping("/profile")
    public ResponseEntity<?> uploadProfile(@Valid @RequestBody MemberDto.UploadProfile profileUploadDto, Authentication authentication) {
        String email = authentication.getName();
        Member updatedMember = memberService.uploadProfile(email, profileUploadDto.getProfileUrl());
        return new ResponseEntity<>(new SingleResponseDto<>(mapper.profileUploadToMember(profileUploadDto)), HttpStatus.OK);
    }

    // 프로필 사진 수정
    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody MemberDto.Update profileUpdateDto, Authentication authentication) {
        String email = authentication.getName();
        Member updatedMember = memberService.updateProfile(email, profileUpdateDto.getProfileUrl());
        return new ResponseEntity<>(new SingleResponseDto<>(mapper.profileUpdateToMember(profileUpdateDto)), HttpStatus.OK);
    }

    // 프로필 사진 삭제
    @DeleteMapping("/profile")
    public ResponseEntity<?> deleteProfile(Authentication authentication) {
        String email = authentication.getName();
        Member updatedMember = memberService.deleteProfile(email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // 삭제 성공 시 응답은 내용이 없으므로 204 No Content
    }

//    @PatchMapping("/role/members/{member-id}")
//    public ResponseEntity<?> updateRole(
//            @PathVariable("member-id") @Positive long memberId,
//            @Valid @RequestBody MemberDto.UpdateRole updateRoleDto,
//            Authentication authentication) {
//        // 권한 수정
//        Member updatedMember = memberService.updateRole(memberId, updateRoleDto.getRole());
//
//        // 수정된 역할 반환
//        return new ResponseEntity<>(
//                new SingleResponseDto<>(mapper.memberToRoleResponse(updatedMember)), HttpStatus.OK);
//    }





}
