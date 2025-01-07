package com.lion.ws.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
@RequestMapping("/crypto")
public class CryptoController {
    @Value("${polygon.io.key}") private String apiKey;

    @GetMapping("/chart")
    public String chart() {
        return "crypto/chart";
    }

    @GetMapping("/msft")
    @ResponseBody
    public Map<String, Object> getBitcoinData() {
        String url = "https://api.polygon.io/v2/aggs/ticker/MSFT/range/1/day/2024-01-01/2024-03-31?apiKey=" + apiKey;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        return response.getBody();
    }
}
