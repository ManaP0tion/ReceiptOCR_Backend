package com.SE11.ReceiptOCR.OCR;

import com.SE11.ReceiptOCR.Receipt.ReceiptDTO;
import com.SE11.ReceiptOCR.Receipt.ReceiptRepository;
import com.SE11.ReceiptOCR.Receipt.Receipt;
import com.SE11.ReceiptOCR.Member.Member;
import com.SE11.ReceiptOCR.Member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/ocr")
public class OCRController {

    private final OCRFunction ocrFunction;
    private final ReceiptRepository receiptRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public OCRController(OCRFunction ocrFunction, ReceiptRepository receiptRepository, MemberRepository memberRepository) {
        this.ocrFunction = ocrFunction;
        this.receiptRepository = receiptRepository;
        this.memberRepository = memberRepository;
    }

    @PostMapping("/process")
    public ResponseEntity<String> processOCR(@RequestParam("file") MultipartFile file,
                                             @RequestParam("userId") String userId) {
        try {
            // OCR 처리
            Map<String, Object> ocrResult = ocrFunction.processOCR(file);

            // OCR 결과를 ReceiptDTO로 변환
            ReceiptDTO receiptDTO = mapOCRResultToReceiptDTO(ocrResult, userId);

            // Receipt 저장
            saveReceipt(receiptDTO);

            return ResponseEntity.ok("OCR processing and Receipt creation completed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process OCR and create Receipt: " + e.getMessage());
        }
    }

    private ReceiptDTO mapOCRResultToReceiptDTO(Map<String, Object> ocrResult, String userId) {
        // OCR 결과에서 필요한 데이터 추출
        String storeName = (String) ocrResult.get("storeName");
        int totalAmount = Integer.parseInt(ocrResult.get("totalAmount").toString());
        String receiptId = (String) ocrResult.get("receiptId");
        String imageUrl = (String) ocrResult.get("imageUrl");
        String dateString = (String) ocrResult.get("date"); // 날짜를 적절히 파싱

        // ReceiptDTO 생성
        return new ReceiptDTO(
                receiptId,
                storeName,
                totalAmount,
                java.time.LocalDate.parse(dateString),
                userId,
                imageUrl
        );
    }

    private void saveReceipt(ReceiptDTO receiptDTO) {
        // Member 조회
        Member member = memberRepository.findById(receiptDTO.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + receiptDTO.getUserId()));

        // Receipt 생성 및 저장
        Receipt receipt = new Receipt();
        receipt.setReceiptId(receiptDTO.getReceiptId());
        receipt.setStoreName(receiptDTO.getStoreName());
        receipt.setTotalAmount(receiptDTO.getTotalAmount());
        receipt.setDate(receiptDTO.getDate());
        receipt.setImageUrl(receiptDTO.getImageUrl());
        receipt.setMember(member);

        receiptRepository.save(receipt);
    }
}