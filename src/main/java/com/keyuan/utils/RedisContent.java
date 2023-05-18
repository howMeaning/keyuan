package com.keyuan.utils;

import cn.hutool.core.io.resource.ClassPathResource;
import org.springframework.util.ClassUtils;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/3/8
 **/

public final class RedisContent {
    public static final String CACHE_GOOD_KEY="cache:good";
    public static final String IMGAGENAME =ClassUtils.getDefaultClassLoader().getResource("").getPath()+ "/static/image";


    public static final String SNAKEKEY="good:snake";
    public static final String OPTIONALKEY="good:optional";

    public static final String RANDOMNUMBER = "order:";

    public static final String STREAM_ORDERNAME="stream:order";

    public static final String CACHE_ORDERNAME="cache:order";

}
