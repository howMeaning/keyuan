package com.keyuan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

/**
 * @descrition:规格Bean
 * @author:how meaningful
 * @date:2023/5/25
 **/
@Table("good_scale")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Scale {
    @TableId
    private Long id;

    /**
     *规模:
     * 和price是一对一的关系
     */
    @TableField("scale")
    private String scale;
    /**
     * 多个
     */
    @TableField("price")
    private String price;

    private Long goodId;

    private Long shopId;

}
