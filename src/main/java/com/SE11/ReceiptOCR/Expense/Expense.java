package com.SE11.ReceiptOCR.Expense;

import com.SE11.ReceiptOCR.Member.Member;
import com.SE11.ReceiptOCR.Receipt.Receipt;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
@Entity
@Setter
@Getter
public class Expense {
    @Id
    private int expense_id;

    private int price;
    private String category;
    private String description;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "receipt_id", nullable = true)
    private Receipt receipt;
}
