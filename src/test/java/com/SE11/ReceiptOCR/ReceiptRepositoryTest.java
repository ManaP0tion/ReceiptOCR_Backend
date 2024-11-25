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
@ActiveProfiles("test") // 테스트 프로파일 활성화
@Rollback(false) // 트랜잭션 롤백 비활성화
public class ReceiptRepositoryTest {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testSaveReceipt() {
        // Member 생성
        Member member = new Member();
        member.setUserId("testMember");
        member.setName("Test Member");
        member.setEmail("testmember@example.com");
        member.setPassword("securepassword");

        // 저장
        memberRepository.save(member);

        // Receipt 생성
        Receipt receipt = new Receipt();
        receipt.setReceiptId("receipt123");
        receipt.setStoreName("Test Store");
        receipt.setTotalAmount(5);
        receipt.setDate(LocalDate.now());
        receipt.setMember(member);

        // 저장
        receiptRepository.save(receipt);

        // 확인
        Optional<Receipt> savedReceipt = receiptRepository.findById("receipt123");
        assertThat(savedReceipt).isPresent();
        assertThat(savedReceipt.get().getStoreName()).isEqualTo("Test Store");
        assertThat(savedReceipt.get().getMember().getName()).isEqualTo("Test Member");
    }
}