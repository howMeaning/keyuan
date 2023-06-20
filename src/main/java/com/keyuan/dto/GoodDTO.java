package com.keyuan.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.keyuan.entity.Scale;
import com.keyuan.entity.Snake;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @descrition: 专门用来前后端交互的类
 * @author:how meaningful
 * @date:2023/5/9
 **/
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GoodDTO {
    /**
     * id,可以没有,数据库自动生成
     */
    private Long id;
    /**
     * 食品名
     */
    private String foodName;
    /**
     * 食品类型
     */
    private String foodType;

    /**
     * 小食:
     * 火腿 2元
     * 鸡腿 6元
     */
    private   List<Snake> foodSnakes;

    /**
     * 初始钱数
     */
    private BigDecimal foodPrice;

    /**
     * 大
     * 中
     * 小
     */
    private  List<ScaleDTO> scales;

    /**
     * 可选:
     * 面条
     * 米粉
     * 河粉
     */
    private List<String> foodOptionals;

    private Integer flavor;

    /**
     * 口味要求,有就是true没有就是false
     */
    private Boolean foodFlavor;


    private Long shopId;

    private Long soleNum;

    /**
     * 照片文件
     */
    private MultipartFile imageFile;

    private String image;
}
