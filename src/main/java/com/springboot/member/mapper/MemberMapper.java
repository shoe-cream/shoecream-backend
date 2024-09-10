package com.springboot.member.mapper;

import com.springboot.exception.BusinessLogicException;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {

    default Member memberPostToMember(MemberDto.Post requestBody){
        Member member = new Member();
        member.setEmployeeId(requestBody.getEmployeeId());
        member.setRoles(List.of(requestBody.getRole()));
        return member;
    }

    Member memberPatchToMember(MemberDto.Patch requestBody);

    @Mapping(source = "profileUrl", target = "profileUrl")
    Member profileUploadToMember(MemberDto.UploadProfile profileUploadDto);

    @Mapping(source = "profileUrl", target = "profileUrl")
    Member profileUpdateToMember(MemberDto.Update profileUpdateDto);


    @Mapping(source = "role", target = "member_roles.roles")

    Member roleUpdateToMember(MemberDto.UpdateRole updateRoleDto);

    // Role 응답 DTO 매핑
    MemberDto.RoleResponse memberToRoleResponse(Member member);

    default Member memberPatchPasswordToMember(MemberDto.PatchPassword requestBody){
        Member member = new Member();
        member.setMemberId(requestBody.getMemberId());
        member.setPassword(requestBody.getNewPassword());
        return member;
    }


    MemberDto.Response memberToMemberResponse(Member member);


    default MemberDto.Response memberToMemberResponseMyPage(Member member){
        MemberDto.Response response = new MemberDto.Response();
        response.setMemberId(member.getMemberId());
        response.setName(member.getName());
        response.setEmail(member.getEmail());
        response.setProfileUrl(member.getProfileUrl());
        if(member.getMemberStatus() == null) {
            response.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);
        } else {
            response.setMemberStatus(member.getMemberStatus());
        }
        return response;
    }




}
