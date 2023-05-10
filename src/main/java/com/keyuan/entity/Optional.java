package com.keyuan.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/9
 **/
@Data
@Table("good_optional")
public class Optional {
    @TableId
    private Long optionalId;
    private String optionalName;
    private Integer optionalMoney;
}
