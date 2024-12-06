package com.SE11.ReceiptOCR.OCR;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class OCR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id; // 기본 키

    private String textResult; // OCR 결과 텍스트

    // 기본 생성자
    public OCR() {}

    // 매개변수를 받는 생성자
    public OCR(String textResult) {
        this.textResult = textResult;
    }

    // Getter 및 Setter 메소드
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTextResult() {
        return textResult;
    }

    public void setTextResult(String textResult) {
        this.textResult = textResult;
    }
}