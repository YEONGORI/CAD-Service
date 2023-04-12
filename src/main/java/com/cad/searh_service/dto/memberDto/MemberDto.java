package com.cad.searh_service.dto.memberDto;

import com.cad.searh_service.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class MemberDto {
    private Long id;
    private String employNumber;
    private String employName;
    private String phoneNumber;
    private String password;
    private String adminKey;

    public static MemberDto fromEntity(Member member)  {
        MemberDto memberDto = MemberDto.builder()
                .id(member.getId())
                .employNumber(member.getEmployNumber())
                .employName(member.getEmployName())
                .phoneNumber(member.getPhoneNumber())
                .adminKey(member.getAdminKey())
                .build();
        return memberDto;
    }
}
