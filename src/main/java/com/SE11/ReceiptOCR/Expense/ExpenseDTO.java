package com.SE11.ReceiptOCR.Expense;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ExpenseDTO {
    private int expense_id;       // 지출 ID
    private int price;            // 금액
    private String description;   // 설명
    private LocalDate date;       // 날짜
    private String userId;       // 사용자 ID
    private Integer subscription_id;
}