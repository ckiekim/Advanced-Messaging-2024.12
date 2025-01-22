package com.lion.ws.service;

import com.lion.ws.entity.StockCodeName;
import com.lion.ws.repository.StockCodeNameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

@Service
public class StockCodeNameService {
    @Autowired private StockCodeNameRepository stockCodeNameRepository;
    @Autowired private ResourceLoader resourceLoader;

    public StockCodeName findByCode(String code) {
        return stockCodeNameRepository.findByCode(code);
    }

    public List<StockCodeName> findByNameContaining(String name) {
        return stockCodeNameRepository.findByNameContaining(name);
    }

    public void insertStockCodeName(StockCodeName stockCodeName) {
        stockCodeNameRepository.save(stockCodeName);
    }

    public void initData(String filename) {
        try {
            Resource resource = resourceLoader.getResource("classpath:/static/data/" + filename);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), Charset.forName("EUC-KR")))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.charAt(0) >= '0' && line.charAt(0) <= '9') {
                        String code = line.substring(0, 6);
                        String name = line.split("\s+")[1];
                        StockCodeName stockCodeName = StockCodeName.builder()
                                .code(code).name(name.substring(12)).build();
                        stockCodeNameRepository.save(stockCodeName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
