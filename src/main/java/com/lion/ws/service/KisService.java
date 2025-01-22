package com.lion.ws.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.ws.repository.StockCodeNameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class KisService {
    @Value("${kis.app.key}") private String kisAppKey;
    @Value("${kis.secret.key}") private String kisSecretKey;
    private static final String realDomainUrl = "https://openapi.koreainvestment.com:9443";
    private String oAuthToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0b2tlbiIsImF1ZCI6IjIzODZhZGVlLWQzNjItNDg5Mi1hMTRlLWNlNWJiMDQzNzA0OCIsInByZHRfY2QiOiIiLCJpc3MiOiJ1bm9ndyIsImV4cCI6MTczNzU5MjE2OSwiaWF0IjoxNzM3NTA1NzY5LCJqdGkiOiJQU3hHTFRraXpPanRyRkhiOU16dHROZnZyNm02TmRDT0xtRVgifQ.EXqPP70I3aj7UhZ10ZPPmLGJOaG6d8rNlwCcAyd_H0wBj2lEw8Z3Fu0j0kKx-kucU46zsNhue1ZiSkcCY-aajQ";

    public Map<String, String> getCurrentPrice(String itemCode) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
//        String oAuthToken = getOAuthToken();
//        System.out.println(oAuthToken);
        headers.set("authorization", "Bearer " + oAuthToken);
        headers.set("appkey", kisAppKey);
        headers.set("appsecret", kisSecretKey);
        headers.set("tr_id", "FHKST01010100");
        headers.set("tr_cont", " ");
        headers.set("custtype", "P");
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(headers);
        String url = realDomainUrl + "/uapi/domestic-stock/v1/quotations/inquire-price" +
                "?FID_COND_MRKT_DIV_CODE=J&FID_INPUT_ISCD=" + itemCode;

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
//            System.out.println("rt_cd=" + jsonNode.get("rt_cd").asText());
//            System.out.println("msg_cd=" + jsonNode.get("msg_cd").asText());
//            System.out.println("msg1=" + jsonNode.get("msg1").asText());

            Map<String, String> output = objectMapper.convertValue(jsonNode.get("output"), Map.class);
            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getStockInfo(String itemCode) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
//        String oAuthToken = getOAuthToken();
//        System.out.println(oAuthToken);
        headers.set("authorization", "Bearer " + oAuthToken);
        headers.set("appkey", kisAppKey);
        headers.set("appsecret", kisSecretKey);
        headers.set("tr_id", "CTPF1002R");
        headers.set("custtype", "P");
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(headers);
        String url = realDomainUrl + "/uapi/domestic-stock/v1/quotations/search-stock-info" +
                "?PRDT_TYPE_CD=300&PDNO=" + itemCode;

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            Map<String, String> output = objectMapper.convertValue(jsonNode.get("output"), Map.class);
            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getKisApprovalKey() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "client_credentials");
        body.put("appkey", kisAppKey);
        body.put("secretkey", kisSecretKey);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    realDomainUrl + "/oauth2/Approval",
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String approvalKey = jsonNode.get("approval_key").asText();
            return approvalKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getOAuthToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "client_credentials");
        body.put("appkey", kisAppKey);
        body.put("appsecret", kisSecretKey);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    realDomainUrl + "/oauth2/tokenP",
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String accessToken = jsonNode.get("access_token").asText();
//            System.out.println("access_token=" + accessToken);
//            System.out.println("token_type=" + jsonNode.get("token_type").asText());
//            System.out.println("expires_in=" + jsonNode.get("expires_in").asText());
            return accessToken;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
