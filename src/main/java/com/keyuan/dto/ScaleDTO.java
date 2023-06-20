package com.keyuan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/26
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ScaleDTO {
    private Long id;

    private String scaleName;

    private BigDecimal scalePrice;

    private Long goodId;

}
