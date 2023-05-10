package com.keyuan;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keyuan.entity.Good;
import com.keyuan.mapper.GoodMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static com.keyuan.utils.RedisContent.CACHE_GOOD_KEY;

@Slf4j
@SpringBootTest
class KeyuanApplicationTests {
	@Autowired
	private GoodMapper goodMapper;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	private ObjectMapper objectMapper = new ObjectMapper();
	@Test
	void contextLoads() {

	}
	/*@Test
	public void testList(){
		List<String> goods = goodMapper.searchAssociation("鸡蛋");
		log.info("菜品:{}",goods);
	}*/

	@Test
	public void testPath() throws FileNotFoundException {
		String path = ResourceUtils.getURL("classPath:").getPath()+ "static/image";
		System.out.println(path);
	}

	@Test
	public void testUUID(){
		String path = "keyuan.png";
		String s = UUID.randomUUID().toString(true) + "." + "png";
		System.out.println(s);

	}

	@Test
	public void testSeleteAll(){
		Set<String> allGood = stringRedisTemplate.opsForZSet().range(CACHE_GOOD_KEY, 1, -1);
		List<Good> collect = allGood.stream().map(s -> {
			try {
				return objectMapper.readValue(s, Good.class);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toList());
		System.out.println(collect);

	}

	/**
	 * 拆分字符串
	 */
	@Test
	public void testString(){
		String s ="你好,啊,里";
		String[] split = s.split(",");
		String s1 = "";
		for (String s2 : split) {
			s1 += s2;
		}
		System.out.println(s1);
	}

	/**
	 * testMap
	 */
	@Test
	public void testMap(){
		Map<String,Object> map  = new HashMap<>();
		map.put("1", "你好");
		map.put("2", "你好");
		map.put("3", "你好");
		map.put("4", "你好");
		List<String> strings = null;
		for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
			strings = Collections.singletonList(stringObjectEntry.getKey());
		}
		System.out.println(strings);
	}
	
	@Test
	public void testGit(){
		System.out.println("new Git");
	}

}
