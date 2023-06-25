package com.keyuan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/6/7
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("t_count")
public class Count {
    @TableField("user_id")
    private Long userId;

    @TableField("pay_password")
    private Integer payPassword;

    private BigDecimal count;

}
