package com.keyuan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/17
 **/
@Data
@Table("t_shop")
public class Shop {
    @TableId
    private Long id;

    private Long typeId;
    @TableField("name")
    private String shopName;

    private String address;

    private Long userId;

    private String function;

    private LocalDateTime openHour;

    //经度
    private Double x;

    //维度
    private Double y;

    private Long sold;

    private BigDecimal avgPrice;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String image;

    private String area;

    @TableField(exist = false)
    private Double distance;

    private String token;

}
