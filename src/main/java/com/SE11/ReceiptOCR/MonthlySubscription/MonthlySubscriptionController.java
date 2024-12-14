package com.SE11.ReceiptOCR.MonthlySubscription;

import com.SE11.ReceiptOCR.Expense.Expense;
import com.SE11.ReceiptOCR.Expense.ExpenseDTO;
import com.SE11.ReceiptOCR.Expense.ExpenseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class MonthlySubscriptionController {

    private final MonthlySubscriptionRepository subscriptionRepository;
    private final ExpenseRepository expenseRepository;

    public MonthlySubscriptionController(MonthlySubscriptionRepository subscriptionRepository, ExpenseRepository expenseRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.expenseRepository = expenseRepository;
    }

    // 1. 구독 추가 (DTO 반환)
    @PostMapping
    public ResponseEntity<MonthlySubscriptionDTO> create(@RequestBody MonthlySubscriptionDTO monthlySubscriptionDTO) {
        // DTO -> 엔티티 변환
        MonthlySubscription subscription = new MonthlySubscription();
        subscription.setSubscriptionItem(monthlySubscriptionDTO.getSubscription_item());
        subscription.setPrice(monthlySubscriptionDTO.getPrice());
        subscription.setBillingDate(monthlySubscriptionDTO.getBilling_date());
        subscription.setStartDate(monthlySubscriptionDTO.getStart_date());
        subscription.setEndDate(monthlySubscriptionDTO.getEnd_date());
        subscription.setUserId(monthlySubscriptionDTO.getUser_id()); // DTO에서 userId 설정

        // 엔티티 저장
        MonthlySubscription createdSubscription = subscriptionRepository.save(subscription);

        // 구독 기간 동안 매월 지출 데이터 생성
        List<Expense> expenses = new ArrayList<>();
        LocalDate currentDate = subscription.getBillingDate();

        while (!currentDate.isAfter(subscription.getEndDate())) {
            Expense expense = new Expense();
            expense.setSubscription(createdSubscription);
            expense.setPrice(subscription.getPrice());
            expense.setDescription(subscription.getSubscriptionItem() + " - Monthly Payment");
            expense.setDate(currentDate);
            expense.setUserId(subscription.getUserId()); // userId 직접 설정
            expenses.add(expense);

            currentDate = currentDate.plusMonths(1); // 다음 달로 이동
        }

        // 지출 데이터 저장
        expenseRepository.saveAll(expenses);

        // 저장된 엔티티 -> DTO 변환
        MonthlySubscriptionDTO subscriptionDTO = convertToDTO(createdSubscription);

        return ResponseEntity.ok(subscriptionDTO);
    }

    // 2. 특정 유저의 구독 목록 조회 (DTO 리스트 반환)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MonthlySubscriptionDTO>> getSubscriptionsByUser(@PathVariable String userId) {
        // userId로 구독 검색
        List<MonthlySubscription> subscriptions = subscriptionRepository.findByUserId(userId);

        // 엔티티 리스트 -> DTO 리스트 변환
        List<MonthlySubscriptionDTO> subscriptionDTOs = subscriptions.stream()
                .map(this::convertToDTO)
                .toList();

        return ResponseEntity.ok(subscriptionDTOs);
    }

    // 3. 특정 구독 ID로 조회 (DTO 반환)
    @GetMapping("/{id}")
    public ResponseEntity<MonthlySubscriptionDTO> getSubscriptionById(@PathVariable int id) {
        // ID로 구독 검색
        MonthlySubscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id: " + id));

        // 엔티티 -> DTO 변환
        MonthlySubscriptionDTO subscriptionDTO = convertToDTO(subscription);

        return ResponseEntity.ok(subscriptionDTO);
    }

    // 4. 구독 정보 수정 (DTO 반환)
    @PutMapping("/{id}")
    public ResponseEntity<MonthlySubscriptionDTO> updateSubscription(
            @PathVariable int id, @RequestBody MonthlySubscriptionDTO monthlySubscriptionDTO) {
        // ID로 기존 구독 검색
        MonthlySubscription existingSubscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id: " + id));

        // 수정된 값 설정
        existingSubscription.setSubscriptionItem(monthlySubscriptionDTO.getSubscription_item());
        existingSubscription.setPrice(monthlySubscriptionDTO.getPrice());
        existingSubscription.setBillingDate(monthlySubscriptionDTO.getBilling_date());
        existingSubscription.setStartDate(monthlySubscriptionDTO.getStart_date());
        existingSubscription.setEndDate(monthlySubscriptionDTO.getEnd_date());
        existingSubscription.setUserId(monthlySubscriptionDTO.getUser_id()); // DTO에서 userId 설정

        // 수정된 엔티티 저장
        MonthlySubscription updatedSubscription = subscriptionRepository.save(existingSubscription);

        // 엔티티 -> DTO 변환
        MonthlySubscriptionDTO subscriptionDTO = convertToDTO(updatedSubscription);

        return ResponseEntity.ok(subscriptionDTO);
    }

    // 5. 구독 삭제 (응답 없음)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable int id) {
        // ID로 구독 존재 여부 확인 후 삭제
        if (subscriptionRepository.existsById(id)) {
            subscriptionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            throw new RuntimeException("Subscription not found with id: " + id);
        }
    }

    // 엔티티를 DTO로 변환하는 메서드
    private MonthlySubscriptionDTO convertToDTO(MonthlySubscription subscription) {
        return new MonthlySubscriptionDTO(
                subscription.getSubscriptionId(),
                subscription.getSubscriptionItem(),
                subscription.getPrice(),
                subscription.getBillingDate(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.getUserId() // DTO에 userId 포함
        );
    }

    // 특정 구독과 연결된 지출 목록 조회
    @GetMapping("/{id}/expenses")
    public ResponseEntity<List<ExpenseDTO>> getExpensesBySubscription(@PathVariable int id) {
        // 구독 ID로 연결된 지출 항목 검색
        List<Expense> expenses = expenseRepository.findBySubscriptionId(id);

        // 엔티티 리스트 -> DTO 리스트 변환
        List<ExpenseDTO> expenseDTOs = expenses.stream()
                .map(expense -> {
                    ExpenseDTO dto = new ExpenseDTO();
                    dto.setExpense_id(expense.getExpenseId());
                    dto.setPrice(expense.getPrice());
                    dto.setDescription(expense.getDescription());
                    dto.setDate(expense.getDate());
                    dto.setUserId(expense.getUserId()); // userId 설정
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(expenseDTOs);
    }
}