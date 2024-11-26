package com.SE11.ReceiptOCR.Expense;

import com.SE11.ReceiptOCR.Member.Member;
import com.SE11.ReceiptOCR.Receipt.Receipt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        expense.setExpense_id(expenseDTO.getExpense_id());
        expense.setPrice(expenseDTO.getPrice());
        expense.setCategory(expenseDTO.getCategory());
        expense.setDescription(expenseDTO.getDescription());
        expense.setDate(expenseDTO.getDate());

        Member member = new Member();
        member.setUserId(expenseDTO.getUser_id());
        expense.setMember(member);

        if (expenseDTO.getReceipt_id() != null) {
            Receipt receipt = new Receipt();
            receipt.setReceiptId(expenseDTO.getReceipt_id());
            expense.setReceipt(receipt);
        }

        Expense savedExpense = expenseRepository.save(expense);
        return ResponseEntity.ok(mapToDTO(savedExpense));
    }

    // 2. 특정 유저의 지출 목록 조회(Read)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpenseDTO>> getExpensesByUser(@PathVariable String userId) {
        List<Expense> expenses = expenseRepository.findByMemberUserId(userId);
        List<ExpenseDTO> expenseDTOs = expenses.stream().map(this::mapToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(expenseDTOs);
    }

    // 3. 특정 지출 ID로 조회(Read)
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDTO> getExpenseById(@PathVariable int id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
        return ResponseEntity.ok(mapToDTO(expense));
    }

    // 4. 지출 정보 수정(Update)
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDTO> updateExpense(@PathVariable int id, @RequestBody ExpenseDTO expenseDTO) {
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));

        existingExpense.setPrice(expenseDTO.getPrice());
        existingExpense.setCategory(expenseDTO.getCategory());
        existingExpense.setDescription(expenseDTO.getDescription());
        existingExpense.setDate(expenseDTO.getDate());

        if (expenseDTO.getReceipt_id() != null) {
            Receipt receipt = new Receipt();
            receipt.setReceiptId(expenseDTO.getReceipt_id());
            existingExpense.setReceipt(receipt);
        } else {
            existingExpense.setReceipt(null);
        }

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
        dto.setExpense_id(expense.getExpense_id());
        dto.setPrice(expense.getPrice());
        dto.setCategory(expense.getCategory());
        dto.setDescription(expense.getDescription());
        dto.setDate(expense.getDate());
        dto.setUser_id(expense.getMember().getUserId());
        if (expense.getReceipt() != null) {
            dto.setReceipt_id(expense.getReceipt().getReceiptId());
        }
        return dto;
    }
}