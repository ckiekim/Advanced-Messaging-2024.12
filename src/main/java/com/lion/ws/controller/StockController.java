package com.lion.ws.controller;

import com.lion.ws.service.StockService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Controller
@RequestMapping("/stock")
public class StockController {
    @Value("${polygon.io.key}") private String polygonApiKey;
    @Value("${profit.com.key}") private String profitApiKey;
    @Autowired private StockService stockService;

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
        result.put("tickerName", stockService.getTickerName(ticker));
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
        result.put("tickerName", stockService.getTickerName(ticker));
        result.put("startDay", startDay);
        result.put("endDay", endDay);
        result.put("data", stockService.handleData(jsonResponse));

        return result;
    }
}
