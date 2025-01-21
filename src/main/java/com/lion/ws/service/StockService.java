package com.lion.ws.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StockService {
    @Value("${polygon.io.key}") private String polygonApiKey;
    @Value("${alphavantage.key}") private String alphavantageApiKey;
    @Value("${profit.com.key}") private String profitApiKey;

    public List<Map<String, Object>> handleData(String jsonData) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> processedData = new ArrayList<>();

        try {
            // JSON 데이터를 리스트로 변환
            List<Map<String, Object>> rawData = objectMapper.readValue(jsonData, List.class);

            for (Map<String, Object> entry : rawData) {
                Map<String, Object> candle = new LinkedHashMap<>();

                // 타임스탬프를 날짜로 변환
                long timestamp = ((Number) entry.get("t")).longValue();
                LocalDate date = Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();

                candle.put("t", date);
                candle.put("o", entry.get("o"));
                candle.put("h", entry.get("h"));
                candle.put("l", entry.get("l"));
                candle.put("c", entry.get("c"));
                candle.put("v", entry.get("v"));

                processedData.add(candle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        processedData.forEach(x -> {
//            System.out.println("t=" + x.get("t") + ", o=" + x.get("o"));
//        });
        return processedData;
    }

    public String getTickerName(String ticker) {
        String apiUrl = "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=" + ticker + "&apikey=" + alphavantageApiKey;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl)).GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.body());
            // "bestMatches" 배열의 첫 번째 객체에서 "2. name" 가져오기
            JsonNode bestMatch = rootNode.get("bestMatches").get(0);
            String name = bestMatch.get("2. name").asText();
            return name;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
