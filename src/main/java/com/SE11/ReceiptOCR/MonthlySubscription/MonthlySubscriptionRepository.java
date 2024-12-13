package com.SE11.ReceiptOCR.MonthlySubscription;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonthlySubscriptionRepository extends JpaRepository<MonthlySubscription, Integer> {
    // userId로 구독 검색
    List<MonthlySubscription> findByUserId(String userId);
}