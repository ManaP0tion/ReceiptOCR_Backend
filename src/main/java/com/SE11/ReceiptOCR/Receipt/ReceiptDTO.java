package com.SE11.ReceiptOCR.Receipt;

import java.time.LocalDate;

public class ReceiptDTO {
    private String receiptId; // 영수증 ID
    private String storeName; // 가게명
    private int totalAmount;  // 물품 수량
    private LocalDate date;   // 날짜
    private String userId;    // 유저 ID

    // 기본 생성자
    public ReceiptDTO() {}

    // 매개변수를 받는 생성자
    public ReceiptDTO(String receiptId, String storeName, int totalAmount, LocalDate date, String userId) {
        this.receiptId = receiptId;
        this.storeName = storeName;
        this.totalAmount = totalAmount;
        this.date = date;
        this.userId = userId;
    }

    // Getter 메소드
    public String getReceiptId() {
        return receiptId;
    }

    public String getStoreName() {
        return storeName;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getUserId() {
        return userId;
    }

    // Setter 메소드
    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}