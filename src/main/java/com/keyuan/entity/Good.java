package com.keyuan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @descrition:专门用来做后端传数据库的类
 * @author:how meaningful
 * @date:2023/3/7
 **/
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Good {
    @TableId
    private Long id;

    private String foodName;

    private String foodPrice;

    private String foodType;

    private Long soleNum;

    private Integer flavorId;

    @TableField(exist = false)
    private String image;

}
