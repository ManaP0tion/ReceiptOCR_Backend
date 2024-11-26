package com.SE11.ReceiptOCR.Income;

import com.SE11.ReceiptOCR.Member.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/incomes")
public class IncomeController {

    private final IncomeRepository incomeRepository;

    public IncomeController(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    // 1. 수익 추가(Create)
    @PostMapping
    public ResponseEntity<IncomeDTO> createIncome(@RequestBody IncomeDTO incomeDTO) {
        Income income = new Income();
        income.setIncome_id(incomeDTO.getIncome_id());
        income.setPrice(incomeDTO.getPrice());
        income.setSource(incomeDTO.getSource());
        income.setDate(incomeDTO.getDate());

        Member member = new Member();
        member.setUserId(incomeDTO.getUser_id());
        income.setMember(member);

        Income savedIncome = incomeRepository.save(income);
        IncomeDTO responseDTO = mapToDTO(savedIncome);
        return ResponseEntity.ok(responseDTO);
    }

    // 2. 특정 유저의 수익 목록 조회(Read)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<IncomeDTO>> getIncomesByUser(@PathVariable String userId) {
        List<Income> incomes = incomeRepository.findByMemberUserId(userId);
        List<IncomeDTO> incomeDTOs = incomes.stream().map(this::mapToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(incomeDTOs);
    }

    // 3. 특정 수익 ID로 조회(Read)
    @GetMapping("/{id}")
    public ResponseEntity<IncomeDTO> getIncomeById(@PathVariable int id) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Income not found with id: " + id));
        return ResponseEntity.ok(mapToDTO(income));
    }

    // 4. 수익 정보 수정(Update)
    @PutMapping("/{id}")
    public ResponseEntity<IncomeDTO> updateIncome(@PathVariable int id, @RequestBody IncomeDTO incomeDTO) {
        Income existingIncome = incomeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Income not found with id: " + id));

        existingIncome.setPrice(incomeDTO.getPrice());
        existingIncome.setSource(incomeDTO.getSource());
        existingIncome.setDate(incomeDTO.getDate());

        Income updatedIncome = incomeRepository.save(existingIncome);
        return ResponseEntity.ok(mapToDTO(updatedIncome));
    }

    // 5. 수익 삭제(Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable int id) {
        incomeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // DTO 매핑 함수
    private IncomeDTO mapToDTO(Income income) {
        IncomeDTO dto = new IncomeDTO();
        dto.setIncome_id(income.getIncome_id());
        dto.setPrice(income.getPrice());
        dto.setSource(income.getSource());
        dto.setDate(income.getDate());
        dto.setUser_id(income.getMember().getUserId());
        return dto;
    }
}