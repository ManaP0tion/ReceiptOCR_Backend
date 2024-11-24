package com.SE11.ReceiptOCR.MonthlySubscription;

import com.SE11.ReceiptOCR.Member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class MonthlySubscription {
    @Id
    private int subscription_id; // 구독 ID

    private String subscription_item; // 구독 항목
    private double price;             // 금액
    private String category;          // 카테고리
    private java.time.LocalDate billing_date; // 결제일

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Member와 연결
    private Member member;
}