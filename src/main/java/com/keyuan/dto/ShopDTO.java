package com.keyuan.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/27
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopDTO {
    @TableId
    private Long id;

    private String shopType;

    private String shopName;

    private String address;

    private String phone;

    private String function;

    private LocalDateTime openHour;

    //经度
    private Double x;

    //维度
    private Double y;

    private Long sold;

    private BigDecimal avgPrice;

    private MultipartFile imageFile;

    private String area;

}
