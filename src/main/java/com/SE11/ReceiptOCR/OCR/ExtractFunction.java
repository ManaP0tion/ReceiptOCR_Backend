package com.SE11.ReceiptOCR.OCR;

import com.SE11.ReceiptOCR.Receipt.ReceiptDTO;
import com.SE11.ReceiptOCR.Expense.ExpenseDTO;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.SE11.ReceiptOCR.shared.ExtractedField;
import com.SE11.ReceiptOCR.shared.BoundingPoly;
import com.SE11.ReceiptOCR.shared.Vertex;



public class ExtractFunction {
    public String price = new String();
    public String Sdate = new String();
    public LocalDate date = LocalDate.now();
    public String store_name = new String();
    public Queue<String> item_name = new ArrayDeque<>();

    public void CUExtractedFields(List<ExtractedField> extractedFields) {
        StringBuilder itemNameBuilder = new StringBuilder();
        int x = 0;
        String line_str = "";
        int cnt=0;

        for (ExtractedField field : extractedFields) {
            line_str += field.getInferText();

            switch (x) {
                case 0:
                    if (line_str.contains("최근영수증발행인쇄")) {
                        x++;
                        line_str = "";
                    }
                    break;
                case 1:
                    if (!store_name.isEmpty()) {
                        store_name += " "; // 기존 문자열이 있을 경우에만 공백 추가
                    }
                    store_name += field.getInferText();
                    if (field.lineBreak()) {
                        x++;
                        line_str = "";
                    }
                    break;
                case 2:
                    if (line_str.contains("일부품목제외")) {
                        x++;
                        line_str = "";
                    }
                    break;
                case 3:
                    if(cnt == 1) {
                        Sdate = field.getInferText();
                        date = parseDate(Sdate);
                        x++;
                    }
                    cnt++;
                    break;
                case 4:
                    if (field.lineBreak()) {
                        x++;
                        line_str = "";
                    }
                    break;
                case 5:
                    itemNameBuilder.append(field.getInferText() + '-');
                    if (line_str.contains("총구매액")) {
                        x++;
                        line_str = "";
                    } else if (field.lineBreak()) {
                        itemNameBuilder.setLength(itemNameBuilder.length() - 1);
                        item_name.offer((itemNameBuilder.toString()));
                        itemNameBuilder.setLength(0);
                        line_str = "";
                    }
                case 6:
                    if (line_str.contains("결제금액")) {
                        x++;
                        line_str = "";
                    }
                    break;
                case 7:
                    if (field.lineBreak()) {
                        price = line_str.trim();
                        return;
                    }
                    break;
            }

            if (field.lineBreak()) {
                line_str = "";
            }
        }
    }

    public void printExtractedData() {
        System.out.println("상점 이름: " + store_name);
        System.out.println("날짜: " + date);
        System.out.println("구매 항목:");
        while (!item_name.isEmpty()) {
            System.out.println(item_name.poll());
        }
        System.out.println("가격: " + price);
    }

    public OCRDTO extractReceipt(String jsonFilePath) {    //version CU
        try {
            // 파일에서 JSON 전체 내용 읽기 (UTF-8로 인코딩된 파일)
            byte[] jsonData = Files.readAllBytes(Paths.get(jsonFilePath));
            String content = new String(jsonData, StandardCharsets.UTF_8);

            // JSON 전체 내용을 JSONObject로 파싱
            JSONObject jsonObject = new JSONObject(content);

            // "images" 키에 해당하는 JSON 배열 추출
            JSONArray imagesArray = jsonObject.getJSONArray("images");

            List<ExtractedField> extractedFields = new ArrayList<>();

            // 각 객체의 "fields" 키에 해당하는 배열에서 데이터를 추출
            for (int i = 0; i < imagesArray.length(); i++) {
                JSONObject imageObject = imagesArray.getJSONObject(i);
                JSONArray fieldsArray = imageObject.getJSONArray("fields");
                //각 단어의 boundingPoly와 inferText,
                for (int j = 0; j < fieldsArray.length(); j++) {
                    JSONObject fieldObject = fieldsArray.getJSONObject(j);

                    String inferText = fieldObject.getString("inferText");
                    boolean lineBreak = fieldObject.getBoolean("lineBreak");

                    JSONArray verticesArray = fieldObject.getJSONObject("boundingPoly").getJSONArray("vertices");
                    List<Vertex> vertices = new ArrayList<>();
                    for (int k = 0; k < verticesArray.length(); k++) {
                        JSONObject vertexObject = verticesArray.getJSONObject(k);
                        float x = (float) vertexObject.getDouble("x");
                        float y = (float) vertexObject.getDouble("y");
                        vertices.add(new Vertex(x, y));
                    }

                    BoundingPoly boundingPoly = new BoundingPoly(vertices);
                    ExtractedField extractedField = new ExtractedField(inferText, lineBreak, boundingPoly);
                    extractedFields.add(extractedField);
                }
            }
            CUExtractedFields(extractedFields);
            OCRDTO processedDTO = new OCRDTO();
            ReceiptDTO receiptDTO = new ReceiptDTO();
            ExpenseDTO expenseDTO = new ExpenseDTO();
            
            // ReceiptDTO 설정
            receiptDTO.setStoreName(store_name);
            receiptDTO.setDate(date);
            receiptDTO.setTotalAmount(Integer.parseInt(price.replaceAll("[^0-9]", "")));
            
            // ExpenseDTO 설정
            expenseDTO.setPrice(Integer.parseInt(price.replaceAll("[^0-9]", "")));
            expenseDTO.setCategory("기타"); // 카테고리는 별도로 처리해야 할 수 있습니다
            expenseDTO.setDescription(String.join(", ", item_name));
            expenseDTO.setDate(date);
            
            processedDTO.setReceiptDTO(receiptDTO);
            processedDTO.setExpenseDTO(expenseDTO);
            
            return processedDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return new OCRDTO(); // 오류 발생 시 빈 OCRDTO 반환
        }
    }

    public static LocalDate parseDate(String dateString) {
        // 요일 정보를 제거합니다.
        String cleanedDate = dateString.split("\\(")[0];
        
        // DateTimeFormatter를 사용하여 날짜 형식을 지정합니다.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // 문자열을 LocalDate로 파싱합니다.
        return LocalDate.parse(cleanedDate, formatter);
    }
}