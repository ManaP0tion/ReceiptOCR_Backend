package com.SE11.ReceiptOCR.OCR;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OCRRepository extends JpaRepository<OCR, String> {
    // 커스텀 쿼리 메소드 추가 가능
}