package com.SE11.ReceiptOCR.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members") // 공통 URL Prefix
public class MemberController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원 등록
     * @param memberDTO 요청 바디로 전달된 회원 데이터
     * @return 성공 메시지
     */
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

        return "Member signup Success";
    }

    /**
     * 회원 조회
     * @param userId 회원 ID
     * @return 회원 정보
     */
    @GetMapping("/{userId}")
    @ResponseBody
    public Member getMember(@PathVariable String userId) {
        Optional<Member> member = memberRepository.findById(userId);
        if (member.isPresent()) {
            return member.get();
        } else {
            throw new IllegalArgumentException("Member not found");
        }
    }

    /**
     * 모든 회원 조회 (테스트용)
     * @return 모든 회원 리스트
     */
    @GetMapping("/all")
    @ResponseBody
    public Iterable<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    /**
     @param userId 업데이트할 회원 ID
     @return 성공메시지
     */
    @PutMapping
    @ResponseBody
    public String updateMember(String userId, @RequestBody MemberDTO memberDTO) {
        Optional<Member> member = memberRepository.findById(userId);

        if(member.isPresent()) {
            Member existingMember = member.get();

            if(memberDTO.getName() != null){
                existingMember.setName(memberDTO.getName());
            }
            if (memberDTO.getEmail() != null) {
                existingMember.setEmail(memberDTO.getEmail());
            }
            if (memberDTO.getPassword() != null) {
                existingMember.setPassword(passwordEncoder.encode(memberDTO.getPassword())); // 패스워드 해싱
            }
            memberRepository.save(existingMember);
            return "Member updated Successfully";
        }
        else {
            throw new IllegalArgumentException("Member not found");
        }
    }




    /**
     * 회원 삭제
     * @param userId 삭제할 회원 ID
     * @return 성공 메시지
     */
    @DeleteMapping("/{userId}")
    @ResponseBody
    public String deleteMember(@PathVariable String userId) {
        memberRepository.deleteById(userId);
        return "Member deleted successfully";
    }
}