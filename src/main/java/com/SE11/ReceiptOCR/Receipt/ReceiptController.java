package com.SE11.ReceiptOCR.Receipt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;

import com.SE11.ReceiptOCR.Member.MemberRepository;
import com.SE11.ReceiptOCR.Member.Member;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    private final ReceiptRepository receiptRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public ReceiptController(ReceiptRepository receiptRepository, MemberRepository memberRepository) {
        this.receiptRepository = receiptRepository;
        this.memberRepository = memberRepository;
    }

    @PostMapping
    public ResponseEntity<String> createReceipt(@RequestBody ReceiptDTO receiptDTO) {
        Receipt receipt = new Receipt();
        receipt.setReceiptId(receiptDTO.getReceiptId());
        receipt.setStoreName(receiptDTO.getStoreName());
        receipt.setTotalAmount(receiptDTO.getTotalAmount());
        receipt.setDate(receiptDTO.getDate());

        // User 객체 조회
        Member member = memberRepository.findById(receiptDTO.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + receiptDTO.getUserId()));

        receipt.setMember(member);
        receiptRepository.save(receipt);
        return ResponseEntity.ok("영수증이 성공적으로 생성되었습니다!");
    }

    @GetMapping("/{receiptId}")
    public ResponseEntity<ReceiptDTO> getReceipt(@PathVariable String receiptId) {
        Receipt receipt = receiptRepository.findById(receiptId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "영수증을 찾을 수 없습니다: " + receiptId));

        ReceiptDTO receiptDTO = new ReceiptDTO(
                receipt.getReceiptId(),
                receipt.getStoreName(),
                receipt.getTotalAmount(),
                receipt.getDate(),
                receipt.getMember().getUser_id() // 수정: getUserId() -> getUser_id()
        );
        return ResponseEntity.ok(receiptDTO);
    }

    @GetMapping
    public ResponseEntity<List<ReceiptDTO>> getAllReceipts() {
        List<Receipt> receipts = receiptRepository.findAll();
        List<ReceiptDTO> receiptDTOs = receipts.stream()
                .map(receipt -> new ReceiptDTO(
                        receipt.getReceiptId(),
                        receipt.getStoreName(),
                        receipt.getTotalAmount(),
                        receipt.getDate(),
                        receipt.getMember().getUser_id() // 수정: getUserId() -> getUser_id()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(receiptDTOs);
    }
}