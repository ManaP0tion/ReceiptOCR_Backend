package com.SE11.ReceiptOCR.Income;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class IncomeDTO {
    private int income_id;   // 수익 ID
    private int price;       // 금액
    private String source;   // 출처
    private LocalDate date;  // 날짜
    private String user_id;  // 사용자 ID
}