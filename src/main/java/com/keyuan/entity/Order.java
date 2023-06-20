package com.keyuan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/3/17
 **/
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table("t_order")
public class Order {
    /**
     * 全局唯一id
     */
    @TableId("order_id")
    private Long orderId;
    /**
     * 随机数
     */
    private Integer orderNumber;
    /**
     * 商品的id,可能有多个
     */
    private String goodId;

    /**
     * 商店名称,前端传
     */
    private Long shopId;

    /**
     * 备注
     */
    private String remark;

    /**
     * userid 前端不需要传
     */
    private Long userId;

    /**
     * 就餐方式 1是堂食 0是打包
     */
    private Integer useType;

    /**
     * 桌号 前端传
     */
    private Integer tableId;
    /**
     * 创建时间 当前的时间
     * 注意:该时间和支付时间是有时间差的
     * 创建时间和支付时间应该有先后顺序(如何保证先后顺序?)
     */
    private LocalDateTime createTime;

    /**
     * 支付时间 当前时间 前端传
     */
    private LocalDateTime payTime;
    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 交易状态
     */
    @TableField(value = "order_status")
    private Integer orderStatus;

    /**
     * 需付金额 前端传
     */
    private BigDecimal requireMoney;

    /**
     * 实付金额 前端传
     */
    private BigDecimal payMoney;

}
