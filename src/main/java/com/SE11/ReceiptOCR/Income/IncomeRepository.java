package com.SE11.ReceiptOCR.Income;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Integer> {
    List<Income> findByMemberUserId(String userId);
}