package com.keyuan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/3/17
 **/
@Data
@Table("order")
@Accessors(chain = true)
public class Order {
    /**
     * 全局唯一id
     */
    @TableId("order_id")
    private Long id;
    /**
     * 随机数
     */
    private Integer orderNumber;

    /**
     * 商店名称,前端传
     */
    private String shopName;

    /**
     * 商家手机号,前端传
     */
    private String shopPhone;

    private Integer tableId;

    /**
     * 支付时间 当前时间 前端传
     */
    private LocalDateTime payTime;

    /**
     * 交易状态
     */
    @TableField(value = "order_status")
    private Integer status;

    /**
     * 创建时间 当前的时间
     * 注意:该时间和支付时间是有时间差的
     * 创建时间和支付时间应该有先后顺序(如何保证先后顺序?)
     */
    private LocalDateTime createTime;

    /**
     * 支付金额 前端传
     */
    private BigDecimal payMoney;
}
