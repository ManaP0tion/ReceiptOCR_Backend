package com.SE11.ReceiptOCR.Member;

import com.SE11.ReceiptOCR.Config.JwtTokenProvider;
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
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원 등록 및 토큰 발급
     * @param memberDTO 요청 바디로 전달된 회원 데이터
     * @return JWT 토큰
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody MemberDTO memberDTO) {
        // DTO -> 엔티티 변환
        Member member = new Member();
        member.setUserId(memberDTO.getUserId());
        member.setName(memberDTO.getName());
        member.setEmail(memberDTO.getEmail());
        member.setPassword(passwordEncoder.encode(memberDTO.getPassword())); // 패스워드 해싱

        // 중복 체크
        if (memberRepository.existsById(memberDTO.getUserId())) {
            return ResponseEntity.status(409).body("User ID already exists");
        }

        // 엔티티 저장
        memberRepository.save(member);

        // JWT 토큰 발급
        String token = jwtTokenProvider.generateToken(member.getUserId());
        return ResponseEntity.ok(token);
    }

    /**
     * 로그인 및 토큰 발급
     * @param loginDTO 요청 바디로 전달된 로그인 데이터
     * @return JWT 토큰
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {

        Optional<Member> memberOptional = memberRepository.findById(loginDTO.getUserId());

        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            if (passwordEncoder.matches(loginDTO.getPassword(), member.getPassword())) {
                // 비밀번호가 일치하면 토큰 발급
                String token = jwtTokenProvider.generateToken(member.getUserId());
                return ResponseEntity.ok(token);
            }
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }


    /**
     * 회원 조회
     * @param userId 회원 ID
     * @return 회원 정보
     */
    @GetMapping("/{userId}")
    public ResponseEntity<MemberDTO> getMember(@PathVariable String userId) {
        Optional<Member> member = memberRepository.findById(userId);

        if (member.isPresent()) {
            Member existingMember = member.get();

            // 엔티티를 DTO로 변환
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setUserId(existingMember.getUserId());
            memberDTO.setName(existingMember.getName());
            memberDTO.setEmail(existingMember.getEmail());

            // 비밀번호는 포함하지 않음
            return ResponseEntity.ok(memberDTO);
        } else {
            return ResponseEntity.status(404).body(null); // 404 NOT FOUND 응답
        }
    }


    /**
     * 모든 회원 조회 (테스트용)
     * @return 모든 회원 리스트
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllMembers() {
        Iterable<Member> members = memberRepository.findAll();

        // 회원이 한 명도 없는 경우 메시지 반환
        if (!members.iterator().hasNext()) {
            return ResponseEntity.status(404).body("등록된 회원이 없습니다");
        }

        return ResponseEntity.ok(members);
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