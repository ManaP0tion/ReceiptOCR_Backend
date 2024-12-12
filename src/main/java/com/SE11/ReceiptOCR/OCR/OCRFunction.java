package com.SE11.ReceiptOCR.OCR;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
public class OCRFunction {

    @Value("${ocr.api.url}")
    private String apiURL;

    @Value("${ocr.secret.key}")
    private String secretKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> processOCR(MultipartFile file) throws Exception {
        // Set up HTTP connection
        URL url = new URL(apiURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setUseCaches(false);
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        con.setRequestProperty("X-OCR-SECRET", secretKey);

        // Create JSON request payload using Jackson
        ObjectNode requestPayload = objectMapper.createObjectNode();
        requestPayload.put("version", "V2");
        requestPayload.put("requestId", java.util.UUID.randomUUID().toString());
        requestPayload.put("timestamp", System.currentTimeMillis());
        requestPayload.put("lang", "ko");

        // Add image data
        ObjectNode image = objectMapper.createObjectNode();
        image.put("format", "jpg"); // Assuming the file is JPG; adjust as needed
        image.put("data", Base64.getEncoder().encodeToString(file.getBytes())); // Convert file bytes to Base64
        image.put("name", "uploaded_image");

        ArrayNode images = objectMapper.createArrayNode();
        images.add(image);
        requestPayload.set("images", images);

        // Send POST request
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.write(objectMapper.writeValueAsBytes(requestPayload));
        }

        // Process response
        int responseCode = con.getResponseCode();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                responseCode == 200 ? con.getInputStream() : con.getErrorStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }

            // Convert response to Map and return
            return objectMapper.readValue(response.toString(), Map.class);
        }
    }
}