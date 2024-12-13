package com.SE11.ReceiptOCR.Member;

import com.SE11.ReceiptOCR.Expense.Expense;
import com.SE11.ReceiptOCR.Income.Income;
import com.SE11.ReceiptOCR.MonthlySubscription.MonthlySubscription;
import com.SE11.ReceiptOCR.Receipt.Receipt;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Member {
    @Id
    @Column(name = "user_id")
    private String userId;     // PK

    private String name;
    private String email;
    private String password;

    @OneToMany(mappedBy = "member")
    private List<Receipt> receipts;

}