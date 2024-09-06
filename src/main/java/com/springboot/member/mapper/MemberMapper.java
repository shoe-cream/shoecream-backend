package com.springboot.member.mapper;

import com.springboot.member.dto.MemberDto;
import com.springboot.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {

    default Member memberPostToMember(MemberDto.Post requestBody){
        Member member = new Member();
        member.setEmail(requestBody.getEmail());
        member.setPassword(requestBody.getPassword());
        member.setName(requestBody.getName());
        member.setEmployeeId(requestBody.getEmployeeId());
        return member;
    }

    Member memberPatchToMember(MemberDto.Patch requestBody);


    default Member memberPatchPasswordToMember(MemberDto.PatchPassword requestBody){
        Member member = new Member();
        member.setMemberId(requestBody.getMemberId());
        member.setPassword(requestBody.getNewPassword());
        return member;
    }



}
