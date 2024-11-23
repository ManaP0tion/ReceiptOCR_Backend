package com.SE11.ReceiptOCR;

import com.SE11.ReceiptOCR.Member.Member;
import com.SE11.ReceiptOCR.Member.MemberRepository;
import com.SE11.ReceiptOCR.Receipt.Receipt;
import com.SE11.ReceiptOCR.Receipt.ReceiptRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test") // MySQL을 사용하는 test 프로파일 활성화
public class ReceiptRepositoryTest {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @Rollback(false) // 데이터 유지
    public void testSaveReceipt() {
        // Member 생성 및 저장
        Member member = new Member();
        member.setUser_id("testMember");
        member.setName("Test Member");
        member.setEmail("testmember@example.com");
        member.setPassword("securepassword");
        memberRepository.save(member);

        // Receipt 생성
        Receipt receipt = new Receipt();
        receipt.setReceiptId("receipt123");
        receipt.setStoreName("Test Store");
        receipt.setTotalAmount(5);
        receipt.setDate(LocalDate.now());
        receipt.setImageUrl("http://example.com/image1.png");
        receipt.setUser(member); // Member와 연관 설정

        // Receipt 저장
        receiptRepository.save(receipt);

        // 저장 확인
        Optional<Receipt> savedReceipt = receiptRepository.findById("receipt123");
        assertThat(savedReceipt).isPresent();
        assertThat(savedReceipt.get().getStoreName()).isEqualTo("Test Store");
        assertThat(savedReceipt.get().getUser().getName()).isEqualTo("Test Member");
    }

    @Test
    @Rollback(false) // 데이터 유지
    public void testFindReceiptByMember() {
        // Member 생성 및 저장
        Member member = new Member();
        member.setUser_id("testMember2");
        member.setName("Another Member");
        member.setEmail("anothermember@example.com");
        member.setPassword("securepassword");
        memberRepository.save(member);

        // Receipt 생성 및 저장
        Receipt receipt = new Receipt();
        receipt.setReceiptId("receipt456");
        receipt.setStoreName("Another Store");
        receipt.setTotalAmount(10);
        receipt.setDate(LocalDate.now());
        receipt.setImageUrl("https://example.com/image2.png");
        receipt.setUser(member); // Member와 연관 설정

        receiptRepository.save(receipt);

        // Member로 Receipt 조회
        Optional<Receipt> foundReceipt = receiptRepository.findById("receipt456");
        assertThat(foundReceipt).isPresent();
        assertThat(foundReceipt.get().getStoreName()).isEqualTo("Another Store");
        assertThat(foundReceipt.get().getUser().getEmail()).isEqualTo("anothermember@example.com");
    }
}