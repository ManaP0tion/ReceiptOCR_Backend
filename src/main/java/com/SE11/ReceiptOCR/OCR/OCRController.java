package com.SE11.ReceiptOCR.OCR;

import com.SE11.ReceiptOCR.Expense.Expense;
import com.SE11.ReceiptOCR.Expense.ExpenseDTO;
import com.SE11.ReceiptOCR.Expense.ExpenseRepository;
import com.SE11.ReceiptOCR.Receipt.ReceiptDTO;
import com.SE11.ReceiptOCR.Receipt.ReceiptRepository;
import com.SE11.ReceiptOCR.Receipt.Receipt;
import com.SE11.ReceiptOCR.Member.Member;
import com.SE11.ReceiptOCR.Member.MemberRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;

//OCR 거의 완 커밋용

@RestController
@RequestMapping("/api/ocr")
public class OCRController {

    private final OCRFunction ocrFunction;
    private final ExtractFunction extractFunction;
    private final ReceiptRepository receiptRepository;
    private final MemberRepository memberRepository;
    private final ExpenseRepository expenseRepository;

    @Autowired
    public OCRController(OCRFunction ocrFunction, ExtractFunction extractFunction,
                         ReceiptRepository receiptRepository, MemberRepository memberRepository, ExpenseRepository expenseRepository) {
        this.ocrFunction = ocrFunction;
        this.extractFunction = extractFunction;
        this.receiptRepository = receiptRepository;
        this.memberRepository = memberRepository;
        this.expenseRepository = expenseRepository;
    }

    public static class ImageRequest {
        private String base64Image;
        private String user;

        // 기본 생성자 추가
        public ImageRequest() {
        }

        public String getBase64Image() {
            return base64Image;
        }
        public String getUser() {
            return user;
        }

        public void setBase64Image(String base64Image) {
            this.base64Image = base64Image;
        }
        public void setUser(String user) {
            this.user = user;
        }
    }

    @PostMapping("/process")
    public ResponseEntity<String> processOCR(@RequestBody ImageRequest imageRequest) {
        try {
            String base64Image = imageRequest.getBase64Image();
            String user = imageRequest.getUser();
            //로컬에 이미지 파일로 저장
            String filePath = saveImage(base64Image, user);

            // OCR 처리
            ocrFunction.processOCR(filePath);

            //OCR된 json 파일이 성공적으로 생성될 때 까지 대기
            String jsonFilePath = filePath + ".json";
            waitForJsonFile(jsonFilePath);

            //extractFunction.
            Map<String, Object> ocrResult = extractFunction.extractData(jsonFilePath);

            // OCR 결과를 ReceiptDTO, ExpenseDTO로 변환
            ReceiptDTO receiptDTO = mapOCRResultToReceiptDTO(ocrResult, user, filePath);
            ExpenseDTO expenseDTO = mapOCRResultToExpenseDTO(ocrResult, user);

            // Receipt, Expense 저장
            saveReceipt(receiptDTO);
            saveExpense(expenseDTO);

            return ResponseEntity.ok("OCR processing and Receipt creation completed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process OCR and create Receipt: " + e.getMessage());
        }
    }

    private String saveImage(String base64Image, String user_id) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String userFolderPath = "images/" + user_id;
        Files.createDirectories(Paths.get(userFolderPath));
        String fileName = user_id + formattedDateTime;
        String filePath = userFolderPath + "/" + fileName;

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        File imageFile = new File(filePath + ".jpg");
        FileUtils.writeByteArrayToFile(imageFile, imageBytes);

        return filePath;
    }

    private void waitForJsonFile(String jsonFilePath) throws Exception {
        File jsonFile = new File(jsonFilePath);
        int maxAttempts = 10;
        int attempts = 0;
        while (!jsonFile.exists() && attempts < maxAttempts) {
            Thread.sleep(1000);
            attempts++;
        }
        if (!jsonFile.exists()) {
            throw new IOException("JSON file was not created after OCR processing");
        }
    }

    private ReceiptDTO mapOCRResultToReceiptDTO(Map<String, Object> ocrResult, String userId, String imageUrl) {
        // OCR 결과에서 필요한 데이터 추출
        String receiptId = imageUrl;
        String storeName = (String) ocrResult.get("storeName");
        int totalAmount = (int) ocrResult.get("totalAmount");
        LocalDate date = (LocalDate) ocrResult.get("date"); // 날짜를 적절히 파싱

        // ReceiptDTO 생성
        return new ReceiptDTO(
                receiptId,
                storeName,
                totalAmount,
                date,
                userId,
                imageUrl + ".jpg"
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

    private ExpenseDTO mapOCRResultToExpenseDTO(Map<String, Object> ocrResult, String userId) {
        // OCR 결과에서 필요한 데이터 추출
        int price = (int) ocrResult.get("price");
        String category = (String) ocrResult.get("category");
        String description = (String) ocrResult.get("description");
        LocalDate date = (LocalDate) ocrResult.get("date"); // 날짜를 적절히 파싱

        // ExpenseDTO 생성
        return new ExpenseDTO(
                price,
                //category,
                description,
                date,
                userId
                //receiptId
        );
    }

    private void saveExpense(ExpenseDTO expenseDTO) {
        // Member 조회
//        Member member = memberRepository.findById(expenseDTO.getUserId())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + expenseDTO.getUserId()));

        // Receipt 조회 (옵션)
//        Receipt receipt = null;
//        if (expenseDTO.getReceipt_id() != null) {
//            receipt = receiptRepository.findById(expenseDTO.getReceipt_id())
//                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receipt not found with ID: " + expenseDTO.getReceipt_id()));
//        }

        // Expense 생성 및 저장
        Expense expense = new Expense();
        expense.setPrice(expenseDTO.getPrice());
        //expense.setCategory(expenseDTO.getCategory());
        expense.setDescription(expenseDTO.getDescription());
        expense.setDate(expenseDTO.getDate());
        expense.setUserId(expenseDTO.getUserId());
//        expense.setMember(member);
//        expense.setReceipt(receipt);

        expenseRepository.save(expense);
    }
}