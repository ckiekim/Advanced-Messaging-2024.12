package com.lion.ws.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverseasMarket {
    private String marketName;
    private String sign;
    private String indexValue;
    private String changeValue;
}
