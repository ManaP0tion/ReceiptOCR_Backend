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

    //기본 생성자
    public ExpenseDTO() {}

    //영수증 인자 받아서 생성
    public ExpenseDTO(int price, String description, LocalDate date, String user_id) {
        this.price = price;
        this.description = description;
        this.date = date;
        this.userId = user_id;
    }
}