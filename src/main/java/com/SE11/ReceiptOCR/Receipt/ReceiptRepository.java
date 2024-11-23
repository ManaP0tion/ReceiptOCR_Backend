package com.SE11.ReceiptOCR.Receipt;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, String> {
    // 커스텀 쿼리 메소드 추가 가능
}