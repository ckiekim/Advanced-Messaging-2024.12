package com.lion.ws.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.ws.entity.InterestGroup;
import com.lion.ws.repository.InterestGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class InterestGroupService {
    @Autowired private InterestGroupRepository interestGroupRepository;
    @Autowired private KisService kisService;
    @Value("${kis.app.key}") private String kisAppKey;
    @Value("${kis.secret.key}") private String kisSecretKey;

    public InterestGroup findById(long id) {
        return interestGroupRepository.findById(id).orElse(null);
    }

    public InterestGroup findByOwnerAndName(String owner, String name) {
        return interestGroupRepository.findByOwnerAndName(owner, name);
    }

    public List<InterestGroup> findByOwner(String owner) {
        return interestGroupRepository.findByOwner(owner);
    }

    public void insertInterestGroup(InterestGroup group) {
        interestGroupRepository.save(group);
    }

    public void updateInterestGroup(InterestGroup group) {
        interestGroupRepository.save(group);
    }

    public void deleteInterestGroup(long id) {
        interestGroupRepository.deleteById(id);
    }

    public List<Map<String, String>> getMultiValue(List<String> itemCodes, String oAuthToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        headers.set("authorization", "Bearer " + oAuthToken);
        headers.set("appkey", kisAppKey);
        headers.set("appsecret", kisSecretKey);
        headers.set("tr_id", "FHKST11300006");
        headers.set("custtype", "P");
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(headers);
        String url = KisService.realDomainUrl + "/uapi/domestic-stock/v1/quotations/intstock-multprice";
        for (int i = 1; i <= itemCodes.size(); i++) {
            url += (i == 1 ? "?" : "&") + "FID_COND_MRKT_DIV_CODE_" + i + "=J&FID_INPUT_ISCD_" + i + "=" + itemCodes.get(i-1);
        }

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            List<Map<String, String>> output = objectMapper.convertValue(jsonNode.get("output"),
                    new TypeReference<List<Map<String, String>>>() {});

            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
