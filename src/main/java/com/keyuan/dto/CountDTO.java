package com.keyuan.dto;

import lombok.Data;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/6/7
 **/
@Data
public class CountDTO {
    private  Long userId;
    private Long shopId;
    private Integer inputPassword;
    private  BigDecimal payMoney;
}
