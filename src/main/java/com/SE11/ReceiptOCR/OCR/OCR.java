package com.SE11.ReceiptOCR.OCR;

import jakarta.persistence.*;

@Entity
@Table(name = "ocr_results") // 테이블 이름 지정
public class OCR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 생성 기본 키
    private Long id; // 기본 키                == > Long타입으로 변경했음

    @Column(nullable = false) // null 불가 제약 조건
    private String textResult; // OCR 결과 텍스트

    // 기본 생성자
    public OCR() {}

    // 매개변수를 받는 생성자
    public OCR(String textResult) {
        this.textResult = textResult;
    }

    // Getter 및 Setter 메소드
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTextResult() {
        return textResult;
    }

    public void setTextResult(String textResult) {
        this.textResult = textResult;
    }
}