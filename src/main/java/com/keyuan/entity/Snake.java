package com.keyuan.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/9
 **/
@Data
@Table("good_snake")
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Snake {
    @TableId
    private Long snakeId;
    private String snakeName;
    private BigDecimal snakeMoney;
    private Long shopId;

}
