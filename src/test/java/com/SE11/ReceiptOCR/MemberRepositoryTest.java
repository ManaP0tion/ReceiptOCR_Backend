package com.SE11.ReceiptOCR;

import com.SE11.ReceiptOCR.Member.Member;
import com.SE11.ReceiptOCR.Member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test") // test 프로파일 사용
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testSaveMember() {
        // 멤버 생성
        Member member = new Member();
        member.setUser_id("mysqlTestUser");
        member.setName("MySQL Test User");
        member.setEmail("mysqltestuser@example.com");
        member.setPassword("securepassword");

        // 저장
        memberRepository.save(member);

        // 저장 확인
        Optional<Member> savedMember = memberRepository.findById("mysqlTestUser");
        assertThat(savedMember).isPresent();
        assertThat(savedMember.get().getName()).isEqualTo("MySQL Test User");
    }

    @Test
    public void testFindMemberById() {
        // 데이터 준비
        Member member = new Member();
        member.setUser_id("mysqlTestUser2");
        member.setName("Another MySQL User");
        member.setEmail("anothermysqluser@example.com");
        member.setPassword("securepassword");

        memberRepository.save(member);

        // 조회
        Optional<Member> foundMember = memberRepository.findById("mysqlTestUser2");
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getEmail()).isEqualTo("anothermysqluser@example.com");
    }
}