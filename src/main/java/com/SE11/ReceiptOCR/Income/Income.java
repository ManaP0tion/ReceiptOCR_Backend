package com.SE11.ReceiptOCR.Income;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Setter
@Getter
public class Income {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int incomeId; // 수익 ID

    private int price;          // 금액
    private String source;      // 출처
    private LocalDate date;     // 날짜

    @Column(name = "user_id", nullable = false)
    private String userId;      // 사용자 ID
}