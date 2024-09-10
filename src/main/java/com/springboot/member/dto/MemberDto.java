package com.springboot.member.dto;

import com.springboot.member.entity.EmployeeId;
import com.springboot.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.springboot.validator.NotSpace;


public class MemberDto {

    @Getter
    @AllArgsConstructor
    public static class Post {
        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String password;

        @NotNull
        private String employeeId;

        @NotBlank
        private String name;

        @NotBlank
        private String authCode;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PatchProfile {
        private long memberId;
        @NotBlank
        private String profileUrl;
    }


    @Getter
    @AllArgsConstructor
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
        private String name;
        private String profileUrl;
        private Member.MemberStatus memberStatus;

        public String getMemberStatus() {
            return memberStatus.getStatus();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Check {
        private boolean isAvailable;
    }


    @Getter
    @Setter
    @AllArgsConstructor
    public static class EmailCheckDto {
        @Email
        private String email;
    }
}