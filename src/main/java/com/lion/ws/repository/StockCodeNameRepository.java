package com.lion.ws.repository;

import com.lion.ws.entity.StockCodeName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockCodeNameRepository extends JpaRepository<StockCodeName, Integer> {
    StockCodeName findByCode(String code);

    List<StockCodeName> findByNameContaining(String name);
}
