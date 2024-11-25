package com.SE11.ReceiptOCR.MonthlySubscription;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MonthlySubscriptionDTO {
    private int subscription_id;          // 구독 ID (응답에만 사용)
    private String subscription_item;     // 구독 항목
    private double price;                 // 금액
    private String category;              // 카테고리
    private LocalDate billing_date;       // 결제일

    // 전체 필드를 포함한 DTO 생성자
    public MonthlySubscriptionDTO(int subscription_id, String subscription_item, double price, String category, LocalDate billing_date) {
        this.subscription_id = subscription_id;
        this.subscription_item = subscription_item;
        this.price = price;
        this.category = category;
        this.billing_date = billing_date;
    }

    // 생성 시 ID 제외 (클라이언트 요청용)
    public MonthlySubscriptionDTO(String subscription_item, double price, String category, LocalDate billing_date) {
        this.subscription_item = subscription_item;
        this.price = price;
        this.category = category;
        this.billing_date = billing_date;
    }

    // 빈 생성자 (필요 시 사용)
    public MonthlySubscriptionDTO() {
    }
}