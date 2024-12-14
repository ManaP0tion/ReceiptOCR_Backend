package com.SE11.ReceiptOCR.Expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    // 특정 유저의 모든 지출 목록 조회
    List<Expense> findByUserId(String userId);

    // 특정 기간 동안의 데이터 조회
    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND e.date BETWEEN :startDate AND :endDate")
    List<Expense> findByUserIdAndDateRange(
            @Param("userId") String userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    // 일별 데이터 조회
    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND YEAR(e.date) = :year AND MONTH(e.date) = :month AND DAY(e.date) = :day")
    List<Expense> findByUserIdAndDate(@Param("userId") String userId, @Param("year") int year, @Param("month") int month, @Param("day") int day);

    // 특정 구독과 연결된 지출 조회
    @Query("SELECT e FROM Expense e WHERE e.subscription.subscriptionId = :subscriptionId")
    List<Expense> findBySubscriptionId(@Param("subscriptionId") int subscriptionId);
}