package com.SE11.ReceiptOCR.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
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
    public ResponseEntity<String> addMember(@RequestBody MemberDTO memberDTO) {
        // DTO -> 엔티티 변환
        Member member = new Member();
        member.setUser_id(memberDTO.getUserId());
        member.setName(memberDTO.getName());
        member.setEmail(memberDTO.getEmail());
        member.setPassword(passwordEncoder.encode(memberDTO.getPassword())); // 패스워드 해싱

        // 엔티티 저장
        memberRepository.save(member);

        return ResponseEntity.ok("Member signup Success");
    }

    /**
     * 회원 조회
     * @param userId 회원 ID
     * @return 회원 정보
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Member> getMember(@PathVariable String userId) {
        Optional<Member> member = memberRepository.findById(userId);
        if (member.isPresent()) {
            return ResponseEntity.ok(member.get());
        } else {
            return ResponseEntity.status(404).body(null); // 404 NOT FOUND 응답
        }
    }

    /**
     * 모든 회원 조회 (테스트용)
     * @return 모든 회원 리스트
     */
    @GetMapping("/all")
    public ResponseEntity<Iterable<Member>> getAllMembers() {
        return ResponseEntity.ok(memberRepository.findAll());
    }

    /**
     * 회원 수정
     * @param userId 업데이트할 회원 ID
     * @param memberDTO 업데이트할 데이터
     * @return 성공 메시지
     */
    @PutMapping("/{userId}")
    public ResponseEntity<String> updateMember(@PathVariable String userId, @RequestBody MemberDTO memberDTO) {
        Optional<Member> member = memberRepository.findById(userId);

        if (member.isPresent()) {
            Member existingMember = member.get();

            if (memberDTO.getName() != null) {
                existingMember.setName(memberDTO.getName());
            }
            if (memberDTO.getEmail() != null) {
                existingMember.setEmail(memberDTO.getEmail());
            }
            if (memberDTO.getPassword() != null) {
                existingMember.setPassword(passwordEncoder.encode(memberDTO.getPassword())); // 패스워드 해싱
            }

            memberRepository.save(existingMember);
            return ResponseEntity.ok("Member updated successfully");
        } else {
            return ResponseEntity.status(404).body("Member not found");
        }
    }

    /**
     * 회원 삭제
     * @param userId 삭제할 회원 ID
     * @return 성공 메시지
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteMember(@PathVariable String userId) {
        if (memberRepository.existsById(userId)) {
            memberRepository.deleteById(userId);
            return ResponseEntity.ok("Member deleted successfully");
        } else {
            return ResponseEntity.status(404).body("Member not found");
        }
    }
}