package com.lion.ws.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PopularStock {
    private String itemName;
    private String sign;
    private String indexValue;
    private String changeValue;
}
