package com.SE11.ReceiptOCR.Member;

import com.SE11.ReceiptOCR.Expense.Expense;
import com.SE11.ReceiptOCR.Income.Income;
import com.SE11.ReceiptOCR.MonthlySubscription.MonthlySubscription;
import com.SE11.ReceiptOCR.Receipt.Receipt;
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
    private String user_id;     // PK

    private String name;
    private String email;
    private String password;

    @OneToMany(mappedBy = "member")
    private List<Expense> expenses;

    @OneToMany(mappedBy = "member")
    private List<Receipt> receipts;

    @OneToMany(mappedBy = "member")
    private List<MonthlySubscription> subscriptions;

    @OneToMany(mappedBy = "member")
    private List<Income> incomes;

}