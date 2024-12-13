package com.SE11.ReceiptOCR.Income;

import com.SE11.ReceiptOCR.Member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Setter
@Getter
public class Income {
    @Id
    private int income_id; // 수익 ID

    private int price;          // 금액
    private String source;      // 출처
    private LocalDate date; // 날짜

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Member와 연결
    private Member member;
}