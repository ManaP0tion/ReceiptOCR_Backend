package com.SE11.ReceiptOCR.MonthlySubscription;

public class MonthlySubscriptionDTO {
    private int subscription_id;
    private String subscription_item; // 구독 항목
    private double price;             // 금액
    private String category;          // 카테고리
    private java.time.LocalDate billing_date; // 결제일

}
