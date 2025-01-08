package com.lion.ws.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
@RequestMapping("/stock")
public class StockController {
    @Value("${polygon.io.key}") private String apiKey;

    @GetMapping("/chart")
    public String chart() {
        return "stock/chart";
    }

    @GetMapping("/apex")
    public String apex() {
        return "stock/apex";
    }

    @GetMapping("/ticker/{ticker}")
    @ResponseBody
    public Map<String, Object> getCandleData(@PathVariable String ticker, @RequestParam String startDay, @RequestParam String endDay) {
        String url = "https://api.polygon.io/v2/aggs/ticker/" + ticker + "/range/1/day/" + startDay + "/" + endDay + "?apiKey=" + apiKey;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        return response.getBody();
    }
}
