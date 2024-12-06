package com.SE11.ReceiptOCR.OCR;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


import org.json.JSONArray;
import org.json.JSONObject;

public class OCRFunction {
    public OCRFunction() {
    }
    public void ocr(String filePath) {
        String apiURL = "https://13zzspooc5.apigw.ntruss.com/custom/v1/35880/d463a920186581276131f3140b4957bcac28d8aad1047d946e47bf7f5142d973/general";
        String secretKey = "Vm5WaFJyYmRpbU9HYXRyQVRobUZvQ21idXJGbnFJdXQ=";
        try {
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            con.setRequestProperty("X-OCR-SECRET", secretKey);

            JSONObject json = new JSONObject();
            json.put("version", "V2");
            json.put("requestId", UUID.randomUUID().toString());
            json.put("timestamp", System.currentTimeMillis());
            json.put("lang", "ko");
            JSONObject image = new JSONObject();
            image.put("format", "jpg");

            // Read image file and convert to byte array
            byte[] imageData = readImageFileToByteArray(filePath + ".jpg");
            if (imageData != null) {
                image.put("data", imageData);
                image.put("name", "demo");
                JSONArray images = new JSONArray();
                images.put(image);
                json.put("images", images);
                String postParams = json.toString();

                // Send POST request
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.write(postParams.getBytes(StandardCharsets.UTF_8));
                wr.flush();
                wr.close();

                // Get response
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if (responseCode == 200) {
                    br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                } else {
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8));
                }

                // Read response
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();

                // Save response to JSON file
                saveResponseToJsonFile(response.toString(), filePath + ".json");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] readImageFileToByteArray(String fileName) {
        try (FileInputStream inputStream = new FileInputStream(fileName)) {
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void saveResponseToJsonFile(String jsonResponse, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, StandardCharsets.UTF_8))) {
            writer.write(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
