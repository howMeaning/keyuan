package com.keyuan;

import cn.hutool.core.bean.BeanUtil;
import com.keyuan.dto.ShopDTO;
import com.keyuan.entity.Shop;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/27
 **/
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class shopTest {
   /* @Test
    public void testShop(){
        ShopDTO shopDTO = new ShopDTO(1L,"肠粉类","肠粉王","光祖北路","12412423","外卖订单", LocalDateTime.of(2023,5,20,18,12,12),12.111, 12.3333, 10, 15L, null,"深圳");
        Shop shop = BeanUtil.copyProperties(shopDTO, Shop.class);
        System.out.println(shop);
    }*/

}
