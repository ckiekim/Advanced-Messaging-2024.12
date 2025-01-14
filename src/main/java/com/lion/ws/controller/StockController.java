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
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/stock")
public class StockController {
    @Value("${polygon.io.key}") private String polygonApiKey;
    @Value("${alphavantage.key}") private String alphavantageApiKey;

    @GetMapping("/chart")
    public String chart() {
        return "stock/chart";
    }

    @GetMapping("/apex")
    public String apex(HttpSession session) {
        session.setAttribute("menu", "stock");
        return "stock/apex";
    }

    @GetMapping("/candleData/{ticker}")
    @ResponseBody
    public Map<String, Object> getCandleData(@PathVariable String ticker,
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
