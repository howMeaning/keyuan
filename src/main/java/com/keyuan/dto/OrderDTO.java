package com.keyuan.dto;

import lombok.Data;
import lombok.experimental.Accessors;


/**
 * @descrition:返回给前端的数据类
 * @author:how meaningful
 * @date:2023/5/17
 **/
@Data
@Accessors(chain = true)
public class OrderDTO {
    /**
     * 全局唯一id
     */
    private Long id;
    /**
     * 订单号 每日生成四位订单号,然后一直递增,需要通过Redis来生成
     */
    private Integer orderNumber;

    /**
     * 交易状态
     */
    private String status;
}
