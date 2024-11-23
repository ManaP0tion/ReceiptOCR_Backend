package com.SE11.ReceiptOCR.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @ResponseBody
    public String addMember(@RequestBody MemberDTO memberDTO) {
        // DTO -> 엔티티 변환
        Member member = new Member();
        member.setUser_id(memberDTO.getUserId());
        member.setName(memberDTO.getName());
        member.setEmail(memberDTO.getEmail());
        member.setPassword(passwordEncoder.encode(memberDTO.getPassword())); // 패스워드 해싱

        // 엔티티 저장
        memberRepository.save(member);

        return "Member signup Success"; // dummy
    }
}