package com.keyuan.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/17
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("shop_type")
public class ShopType {
    @TableId
    private Long id;

    private String TypeName;

}
