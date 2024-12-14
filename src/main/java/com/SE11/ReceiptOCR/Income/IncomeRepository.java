package com.SE11.ReceiptOCR.Income;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Integer> {

    // 특정 유저의 모든 수익 목록 조회
    List<Income> findByUserId(String userId);

    // 특정 유저의 기간별 수익 조회
    @Query("SELECT i FROM Income i WHERE i.userId = :userId AND i.date BETWEEN :startDate AND :endDate")
    List<Income> findByUserIdAndDateBetween(@Param("userId") String userId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
}
