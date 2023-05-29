package com.keyuan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/26
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScaleDTO {
    private Long id;

    private String scale;

    private BigDecimal price;

    private Long goodId;

}
