package com.SE11.ReceiptOCR.Expense;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;

    public ExpenseController(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    // 1. 지출 추가(Create)
    @PostMapping
    public ResponseEntity<ExpenseDTO> createExpense(@RequestBody ExpenseDTO expenseDTO) {
        Expense expense = new Expense();
        expense.setExpenseId(expenseDTO.getExpense_id());
        expense.setPrice(expenseDTO.getPrice());
        expense.setDescription(expenseDTO.getDescription());
        expense.setDate(expenseDTO.getDate());
        expense.setUserId(expenseDTO.getUserId()); // userId 직접 설정

        Expense savedExpense = expenseRepository.save(expense);
        return ResponseEntity.ok(mapToDTO(savedExpense));
    }

    // 2. 특정 유저의 지출 목록 조회(Read)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpenseDTO>> getExpensesByUser(@PathVariable String userId) {
        List<Expense> expenses = expenseRepository.findByUserId(userId);
        List<ExpenseDTO> expenseDTOs = expenses.stream().map(this::mapToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(expenseDTOs);
    }

    // 3. 특정 기간 동안의 지출 조회(Read)
    @GetMapping("/user/{userId}/range")
    public ResponseEntity<List<ExpenseDTO>> getExpensesByDateRange(
            @PathVariable String userId,
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr) {
        // 문자열 날짜 -> LocalDate 변환
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        // 리포지토리 호출
        List<Expense> expenses = expenseRepository.findByUserIdAndDateRange(userId, startDate, endDate);

        // DTO 변환
        List<ExpenseDTO> expenseDTOs = expenses.stream().map(this::mapToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(expenseDTOs);
    }

    // 4. 지출 정보 수정(Update)
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDTO> updateExpense(@PathVariable int id, @RequestBody ExpenseDTO expenseDTO) {
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));

        existingExpense.setPrice(expenseDTO.getPrice());
        existingExpense.setDescription(expenseDTO.getDescription());
        existingExpense.setDate(expenseDTO.getDate());

        Expense updatedExpense = expenseRepository.save(existingExpense);
        return ResponseEntity.ok(mapToDTO(updatedExpense));
    }

    // 5. 지출 삭제(Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable int id) {
        expenseRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // DTO 매핑 함수
    private ExpenseDTO mapToDTO(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setExpense_id(expense.getExpenseId());
        dto.setPrice(expense.getPrice());
        dto.setDescription(expense.getDescription());
        dto.setDate(expense.getDate());
        dto.setUserId(expense.getUserId()); // userId 직접 매핑
        return dto;
    }
}