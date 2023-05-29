package com.keyuan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/24
 **/
@Data
@Table("t_user")
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 微信名称,不一定要刷新
     */
    private String name;
    /**
     * 商品id 可以为null
     */
    private Long shopId;
    /**
     * 电话
     */
    private String phone;

    /**
     * 头像
     */
    private String icon;

    private Double x;

    private Double y;

    private String area;

    @TableField(exist = false)
    private String token;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
