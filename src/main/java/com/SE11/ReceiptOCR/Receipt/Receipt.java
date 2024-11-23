package com.SE11.ReceiptOCR.Receipt;

import com.SE11.ReceiptOCR.Member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Receipt {

    @Id
    @Column(name = "receipt_id", length = 50, nullable = false)
    private String receiptId; // 영수증 ID

    @Column(name = "store_name", length = 100, nullable = false)
    private String storeName; // 가게명

    @Column(name = "total_amount", nullable = false)
    private int totalAmount; // 물품 수량

    @Column(name = "date", nullable = false)
    private LocalDate date; // 날짜

    @Column(name = "image_url", length = 512)
    private String imageUrl; // 이미지 주소

    @ManyToOne(fetch = FetchType.LAZY) // Member와 연관 관계 설정
    @JoinColumn(name = "user_id", nullable = false) // 외래 키
    private Member user; // 유저 엔티티와의 관계
}