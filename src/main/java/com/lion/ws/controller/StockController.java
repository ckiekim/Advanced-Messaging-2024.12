package com.lion.ws.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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

@Controller
@RequestMapping("/stock")
public class StockController {
    @Value("${polygon.io.key}") private String polygonApiKey;
    @Value("${alphavantage.key}") private String alphavantageApiKey;
    @Value("${profit.com.key}") private String profitApiKey;

    @GetMapping("/chart")
    public String chart() {
        return "stock/chart";
    }

    @GetMapping("/polygon")
    public String polygon(HttpSession session) {
        session.setAttribute("menu", "stock");
        return "stock/polygon";
    }

    @GetMapping("/candleData/{ticker}")
    @ResponseBody
    public Map<String, Object> getPolygonCandleData(@PathVariable String ticker,
                                                    @RequestParam(name="s", defaultValue = "2024-07-01") String startDay,
                                                    @RequestParam(name="e", defaultValue = "2024-12-31") String endDay) {
        String url = "https://api.polygon.io/v2/aggs/ticker/" + ticker + "/range/1/day/" + startDay + "/" + endDay + "?apiKey=" + polygonApiKey;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("tickerName", getTickerName(ticker));
        result.put("startDay", startDay);
        result.put("endDay", endDay);
        result.put("data", response.getBody());

        return result;
    }

    @GetMapping("/profit")
    public String profit() {
        return "stock/profit";
    }

    @GetMapping("/candleData2/{ticker}")
    @ResponseBody
    public Map<String, Object> getProfitCandleData(@PathVariable String ticker,
                                     @RequestParam(name="s", defaultValue = "2024-07-01") String startDay,
                                     @RequestParam(name="e", defaultValue = "2024-12-31") String endDay) {
        String url = "https://api.profit.com/data-api/market-data/historical/daily/" + ticker + "?token=" + profitApiKey + "&start_date=" + startDay + "&end_date=" + endDay;
        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.getForObject(url, String.class);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("tickerName", getTickerName(ticker));
        result.put("startDay", startDay);
        result.put("endDay", endDay);
        result.put("data", handleData(jsonResponse));

        return result;
    }

    private List<Map<String, Object>> handleData(String jsonData) {
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

    private String getTickerName(String ticker) {
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
