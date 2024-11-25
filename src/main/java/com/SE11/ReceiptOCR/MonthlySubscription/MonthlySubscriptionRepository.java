package com.SE11.ReceiptOCR.MonthlySubscription;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonthlySubscriptionRepository extends JpaRepository<MonthlySubscription, Integer> {
    // Member의 user_id 기준 검색
    List<MonthlySubscription> findByMemberUserId(String userId);
}