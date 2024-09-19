package com.springboot.member.dto;

import com.springboot.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;

import com.springboot.validator.NotSpace;

import java.util.List;


public class MemberDto {

    @Getter
    @AllArgsConstructor
    public static class Post {

        private String employeeId;
        private String role;

    }

    @Getter
    @Setter
    public static class UploadProfile {
        private String profileUrl;  // 업로드할 프로필 사진 URL
    }

    @Getter
    @Setter
    public static class Update {
        private String profileUrl;  // 업데이트할 새 프로필 사진 URL
    }

    @Getter
    @Setter
    public static class UpdateRole {
        private String role;  // 수정할 역할
    }

    @Getter
    @Setter
    public static class RoleResponse {
        private String role;  // 현재 역할 반환
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Patch {

        private long memberId;

        @NotSpace(message = "회원 이름은 공백이 아니어야 합니다")
        private String name;
        private String tel;
        private String address;

        public void setMemberId(long memberId) {
            this.memberId = memberId;
        }

    }

    @Getter
    @AllArgsConstructor
    public static class PatchPassword {
        private long memberId;

        private String password;

        private String newPassword;

        public void setMemberId(long memberId) {
            this.memberId = memberId;
        }

    }

    @AllArgsConstructor
    @Getter
    @NoArgsConstructor
    @Setter
    public static class Response {
        private long memberId;
        private String email;
        private String tel;
        private String address;
        private String employeeId;
        private String name;
        private String profileUrl;
        private Member.MemberStatus memberStatus = Member.MemberStatus.MEMBER_ACTIVE;
        private List<String> roles;
    }

    @Getter
    @AllArgsConstructor
    public static class Check {
        private boolean isAvailable; // 중복 여부
        private String message;  // 메시지 필드 추가
    }


    @Getter
    @Setter
    @AllArgsConstructor
    public static class EmailCheckDto {
        @Email
        private String email;
    }
}