package com.SE11.ReceiptOCR.OCR;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.HashMap;


import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.SE11.ReceiptOCR.shared.ExtractedField;
import com.SE11.ReceiptOCR.shared.BoundingPoly;
import com.SE11.ReceiptOCR.shared.Vertex;
import org.springframework.stereotype.Component;


@Component
public class ExtractFunction {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, Object> CUExtractedFields(List<ExtractedField> extractedFields) {
        Map<String, Object> result = new HashMap<>();
        StringBuilder itemNameBuilder = new StringBuilder();
        String line_str = "";
        int x = 0;
        int cnt = 0;
        List<String> items = new ArrayList<>();

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
                    if (result.get("storeName") == null) {
                        result.put("storeName", "");
                    }
                    result.put("storeName", result.get("storeName") + " " + field.getInferText());
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
                    if (cnt == 1) {
                        result.put("date", parseDate(field.getInferText()));
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
                    itemNameBuilder.append(field.getInferText()).append('-');
                    if (line_str.contains("총구매액")) {
                        x++;
                        line_str = "";
                    } else if (field.lineBreak()) {
                        itemNameBuilder.setLength(itemNameBuilder.length() - 1);
                        items.add(itemNameBuilder.toString());
                        itemNameBuilder.setLength(0);
                        line_str = "";
                        //개수 추가될 때마다 업데이트
                        result.put("totalAmount", items.size());
                    }
                    break;
                case 6:
                    if (line_str.contains("결제금액")) {
                        x++;
                        line_str = "";
                    }
                    break;
                case 7:
                    if (field.lineBreak()) {
                        result.put("price", Integer.parseInt(line_str.trim().replaceAll("[^0-9]", "")));
                        x++;
                    }
                    break;
            }

            if (field.lineBreak()) {
                line_str = "";
            }
        }

        result.put("category", "CU");
        result.put("description", String.join(", ", items));
        printResultMap(result); // 결과 출력

        return result;
    }

    public Map<String, Object> extractData(String jsonFilePath) {
        try {
            byte[] jsonData = Files.readAllBytes(Paths.get(jsonFilePath));
            String content = new String(jsonData, StandardCharsets.UTF_8);

            Map<String, Object> jsonMap = objectMapper.readValue(content, Map.class);
            List<Map<String, Object>> imagesArray = (List<Map<String, Object>>) jsonMap.get("images");

            List<ExtractedField> extractedFields = new ArrayList<>();

            for (Map<String, Object> imageObject : imagesArray) {
                List<Map<String, Object>> fieldsArray = (List<Map<String, Object>>) imageObject.get("fields");
                for (Map<String, Object> fieldObject : fieldsArray) {
                    String inferText = (String) fieldObject.get("inferText");
                    boolean lineBreak = (boolean) fieldObject.get("lineBreak");

                    Map<String, Object> boundingPolyMap = (Map<String, Object>) fieldObject.get("boundingPoly");
                    List<Map<String, Object>> verticesArray = (List<Map<String, Object>>) boundingPolyMap.get("vertices");
                    List<Vertex> vertices = new ArrayList<>();
                    for (Map<String, Object> vertexObject : verticesArray) {
                        float x = ((Number) vertexObject.get("x")).floatValue();
                        float y = ((Number) vertexObject.get("y")).floatValue();
                        vertices.add(new Vertex(x, y));
                    }
                    BoundingPoly boundingPoly = new BoundingPoly(vertices);
                    ExtractedField extractedField = new ExtractedField(inferText, lineBreak, boundingPoly);
                    extractedFields.add(extractedField);
                }
            }

            return CUExtractedFields(extractedFields);
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
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

    public void printResultMap(Map<String, Object> result) {
        System.out.println("Extracted Data:");
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}