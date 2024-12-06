package com.SE11.ReceiptOCR.OCR;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.SE11.ReceiptOCR.Expense.Expense;
import com.SE11.ReceiptOCR.Expense.ExpenseRepository;
import com.SE11.ReceiptOCR.Receipt.Receipt;
import com.SE11.ReceiptOCR.Receipt.ReceiptRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class OCRController {

    @Autowired
    private ReceiptRepository receiptRepository;
    @Autowired
    private ExpenseRepository expenseRepository;

    @PostMapping("/api/ocrs")
    public ResponseEntity<String> ocrImage(@RequestBody OCRDTO ocrDTO) {
        try {
            OCRFunction ocrFunction = new OCRFunction();
            ExtractFunction extractFunction = new ExtractFunction();
            // 이미지 처리 로직
            String base64Image = ocrDTO.getBase64Data();
            String user_id = ocrDTO.getReceiptDTO().getUserId();
            System.out.println("Received image for user: " + user_id);
            
            // 이미지 저장
            String filePath = saveImage(base64Image, user_id);

            System.out.println("Image saved successfully for user: " + user_id);

            // OCR 함수 실행
            ocrFunction.ocr(filePath);

            // JSON 파일 처리 대기
            String jsonFilePath = filePath + ".json";
            waitForJsonFile(jsonFilePath);

            // JSON 데이터 추출 및 DTO 업데이트
            OCRDTO processedDTO = extractFunction.extractReceipt(jsonFilePath);

            // DB에 저장
            Receipt receipt = new Receipt(processedDTO.getReceiptDTO());
            Expense expense = new Expense();
            expense.initFromDTO(processedDTO.getExpenseDTO());
            expense.setReceipt(receipt);
            
            receiptRepository.save(receipt);
            expenseRepository.save(expense);

            return new ResponseEntity<>("OCR processing and data saving completed successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to process OCR and save data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
}
