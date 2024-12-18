package com.SE11.ReceiptOCR.Expense;

import com.SE11.ReceiptOCR.MonthlySubscription.MonthlySubscription;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Setter
@Getter
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private int expenseId;

    private int price;
    private String description;
    private LocalDate date;

    @Column(name = "user_id", nullable = false)
    private String userId; // Member 참조 대신 userId 필드로 변경

    @ManyToOne
    @JoinColumn(name = "subscription_id", nullable = true)
    private MonthlySubscription subscription; // MonthlySubscription과 연결
}