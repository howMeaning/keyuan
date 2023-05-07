package com.keyuan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/3/8
 **/
@Accessors(chain = true)
@Data
public class GoodType {
    @TableId(value = "typeId")
    private Integer id;
    private String typeName;
}
