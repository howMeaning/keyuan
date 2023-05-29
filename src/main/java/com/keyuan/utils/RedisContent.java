package com.keyuan.utils;

import cn.hutool.core.io.resource.ClassPathResource;
import org.springframework.util.ClassUtils;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/3/8
 **/

public final class RedisContent {
    public static final String IMGAGENAME =ClassUtils.getDefaultClassLoader().getResource("").getPath()+ "/static/image";

    public static final String SNAKEKEY="good:snake:";

    public static final String SCALEKEY="good:scale:";

    public static final String RANDOMNUMBER = "order:random:";

    public static final String CACHEGOOD = "cache:good:";

    public static final String RANKKEY = "good:rank:";

    public static final String CACHE_ORDERNAME="cache:order:userid:";

    public static final String LOCKKEY = "lock:order:";

    public static final String CACHESHOPGEO = "shop:geo:";

    public static final String CACHEUSER = "cache:user:";




}
