package com.lion.ws.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.ws.entity.KisOAuthToken;
import com.lion.ws.repository.KisOAuthTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KisOAuthTokenService {
    @Autowired private KisOAuthTokenRepository kisOAuthTokenRepository;
    @Value("${kis.app.key}") private String kisAppKey;
    @Value("${kis.secret.key}") private String kisSecretKey;

    public String getToken() {
        String oAuthToken;
        List<KisOAuthToken> tokenList = kisOAuthTokenRepository.findAll();
        if (tokenList == null || tokenList.size() == 0) {
            oAuthToken = saveAndGetOAuthToken();
            return oAuthToken;
        }
        KisOAuthToken kisOAuthToken = tokenList.get(0);
        LocalDateTime now = LocalDateTime.now();
        // OAuthToken을 발급받은지 6시간 이후이고, 날짜가 1일 더 많으면 신규로 발급 받음
        if (now.isAfter(kisOAuthToken.getTimestamp().plusHours(6)) &&
            now.toLocalDate().isEqual(kisOAuthToken.getTimestamp().toLocalDate().plusDays(1))) {
            kisOAuthTokenRepository.deleteById(kisOAuthToken.getId());
            oAuthToken = saveAndGetOAuthToken();
            return oAuthToken;
        }
        return kisOAuthToken.getToken();
    }

    private String saveAndGetOAuthToken() {
        String oAuthToken = getOAuthToken();
        KisOAuthToken kisOAuthToken = KisOAuthToken.builder()
                .token(oAuthToken).timestamp(LocalDateTime.now())
                .build();
        kisOAuthTokenRepository.save(kisOAuthToken);
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
                    KisService.realDomainUrl + "/oauth2/tokenP",
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String accessToken = jsonNode.get("access_token").asText();
            return accessToken;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
