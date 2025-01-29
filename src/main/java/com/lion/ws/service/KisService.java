package com.lion.ws.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.ws.repository.StockCodeNameRepository;
import jakarta.servlet.http.HttpSession;
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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class KisService {
    @Value("${kis.app.key}") private String kisAppKey;
    @Value("${kis.secret.key}") private String kisSecretKey;
    public static final String realDomainUrl = "https://openapi.koreainvestment.com:9443";

    public Map<String, String> getCurrentPrice(String itemCode, String oAuthToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
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

    public Map<String, String> getStockInfo(String itemCode, String oAuthToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
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

    public String handleOAuthToken(HttpSession session) {
        String oAuthToken = (String) session.getAttribute("OAuthToken");
        oAuthToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0b2tlbiIsImF1ZCI6IjBjYzEzMjU2LTYzYTMtNGRlZS04MjljLTg0OThjYzdjNDRmNSIsInByZHRfY2QiOiIiLCJpc3MiOiJ1bm9ndyIsImV4cCI6MTczODE5MjIyNSwiaWF0IjoxNzM4MTA1ODI1LCJqdGkiOiJQU3hHTFRraXpPanRyRkhiOU16dHROZnZyNm02TmRDT0xtRVgifQ.O5P3CSSjYdRaIxC-HnWjZB_F0ZqyfkALSZNaoxAOr8bk6-3i7Q5-IrVoyXOFZ8hyduyt1BM2kTlXctS6s-Rscw";
        if (oAuthToken == null || oAuthToken.isEmpty()) {
            oAuthToken = getOAuthToken();
            System.out.println("OAuthToken=" + oAuthToken);
            session.setAttribute("OAuthToken", oAuthToken);
            session.setMaxInactiveInterval(24 * 60 * 60);
        }
        return oAuthToken;
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

    public Map<String, Object> getDailyCandle(String itemCode, String oAuthToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        headers.set("authorization", "Bearer " + oAuthToken);
        headers.set("appkey", kisAppKey);
        headers.set("appsecret", kisSecretKey);
        headers.set("tr_id", "FHKST03010100");
        headers.set("custtype", "P");
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(headers);
        LocalDate endDate = LocalDate.now().minusDays(1L);
        LocalDate startDate = endDate.minusYears(1L);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String url = realDomainUrl + "/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice" +
                "?FID_COND_MRKT_DIV_CODE=J&FID_INPUT_ISCD=" + itemCode +
                "&FID_INPUT_DATE_1=" + startDate.format(formatter) + "&FID_INPUT_DATE_2=" + endDate.format(formatter) +
                "&FID_PERIOD_DIV_CODE=D&FID_ORG_ADJ_PRC=0";

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            Map<String, String> output1 = objectMapper.convertValue(jsonNode.get("output1"), Map.class);
            List<Map<String, String>> output2 = objectMapper.convertValue(jsonNode.get("output2"),
                    new TypeReference<List<Map<String, String>>>() {});
            Collections.reverse(output2); // Reverse the list order

            Map<String, Object> output = new LinkedHashMap<>();
            output.put("output1", output1);
            output.put("output2", output2);
            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Object> getMinuteCandleTillNow(String itemCode, String oAuthToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        headers.set("authorization", "Bearer " + oAuthToken);
        headers.set("appkey", kisAppKey);
        headers.set("appsecret", kisSecretKey);
        headers.set("tr_id", "FHKST03010200");
        headers.set("custtype", "P");
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(headers);

        // 개장시간 09:00 부터 현재 또는 폐장시간 15:30 까지 30분 단위로 호출
        // output2 에는 09:00 부터 현재까지 1분 단위의 데이터가 있게
        // time 값을 090000, 093000, 100000, ... 현재 시분초까지 변경하며 호출
        List<Map<String, String>> allTimeData = new ArrayList<>();
        Map<String, String> output1 = null;
        LocalTime endTime = LocalTime.of(15, 30);

        try {
            LocalTime startTime = LocalTime.of(9, 29);
            LocalTime currentTime = LocalTime.now();

            while (true) {
                String time = startTime.format(DateTimeFormatter.ofPattern("HHmmss"));
                String url = realDomainUrl + "/uapi/domestic-stock/v1/quotations/inquire-time-itemchartprice" +
                        "?FID_ETC_CLS_CODE=&FID_COND_MRKT_DIV_CODE=J&FID_INPUT_ISCD=" + itemCode +
                        "&FID_INPUT_HOUR_1=" + time + "&FID_PW_DATA_INCU_YN=N";

                ResponseEntity<String> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        String.class
                );
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                output1 = objectMapper.convertValue(jsonNode.get("output1"), Map.class);
                List<Map<String, String>> output2 = objectMapper.convertValue(jsonNode.get("output2"),
                        new TypeReference<List<Map<String, String>>>() {});
                Collections.reverse(output2); // Reverse the list order

                if (startTime.isAfter(currentTime) || startTime.isAfter(endTime)) {
                    LocalTime effectiveTime = currentTime.isBefore(endTime) ? currentTime : endTime;
                    int diff = (int) Duration.between(startTime, effectiveTime.plusMinutes(30)).toMinutes();
                    for (int i = 0; i < diff; i++)
                        allTimeData.add(output2.get(i));
                    break;
                } else {
                    allTimeData.addAll(output2);
                }
                startTime = startTime.plusMinutes(30);
            }
            Map<String, Object> output = new LinkedHashMap<>();
            output.put("output1", output1);
            output.put("output2", allTimeData);
            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
