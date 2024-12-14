package com.SE11.ReceiptOCR.MonthlySubscription;

import com.SE11.ReceiptOCR.Expense.Expense;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Setter
@Getter
public class MonthlySubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int subscriptionId; // 구독 ID

    private String subscriptionItem; // 구독 항목
    private int price;               // 금액
    private LocalDate billingDate;   // 첫 결제일
    private LocalDate startDate;     // 구독 시작일
    private LocalDate endDate;       // 구독 종료일

    @Column(nullable = false)
    private String userId; // 사용자 ID를 직접 저장

    @JsonIgnore
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses; // 연결된 지출 항목
}