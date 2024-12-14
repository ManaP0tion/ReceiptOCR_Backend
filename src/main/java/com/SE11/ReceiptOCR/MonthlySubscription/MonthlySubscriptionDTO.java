package com.SE11.ReceiptOCR.MonthlySubscription;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MonthlySubscriptionDTO {
    private int subscription_id;          // 구독 ID
    private String subscription_item;     // 구독 항목
    private int price;                    // 금액
    private LocalDate billing_date;       // 첫 결제일
    private LocalDate start_date;         // 구독 시작일
    private LocalDate end_date;           // 구독 종료일
    private String user_id;               // 유저 ID (클라이언트로부터 전달받음)

    public MonthlySubscriptionDTO() {}

    public MonthlySubscriptionDTO(int subscription_id, String subscription_item, int price, LocalDate billing_date, LocalDate start_date, LocalDate end_date, String user_id) {
        this.subscription_id = subscription_id;
        this.subscription_item = subscription_item;
        this.price = price;
        this.billing_date = billing_date;
        this.start_date = start_date;
        this.end_date = end_date;
        this.user_id = user_id;
    }
}