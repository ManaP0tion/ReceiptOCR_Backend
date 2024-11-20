package com.SE11.ReceiptOCR.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @ResponseBody
    public String addMember(String user_id,
                            String name,
                            String email,
                            String password) {
        Member member = new Member();
        member.setUser_id(user_id);
        member.setName(name);
        member.setEmail(email);
        var hash = passwordEncoder.encode(password);
        member.setPassword(hash);

        memberRepository.save(member);

        return "ok";        // dummy
    }
}
