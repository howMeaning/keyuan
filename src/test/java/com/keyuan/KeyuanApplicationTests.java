package com.keyuan;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keyuan.dto.GoodDTO;
import com.keyuan.dto.Result;
import com.keyuan.entity.Good;
import com.keyuan.entity.Scale;
import com.keyuan.entity.Snake;
import com.keyuan.mapper.GoodMapper;
import com.keyuan.utils.RedisContent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.keyuan.utils.RedisContent.CACHEGOOD;


@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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


/*	*//**
	 * 拆分字符串
	 *//*
	@Test
	public void testString(){
		String s ="你好,啊,里";
		String[] split = s.split(",");
		String s1 = "";
		for (String s2 : split) {
			s1 += s2;
		}
		System.out.println(s1);
	}*/

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

/*	@Test
	public void testBean(){
		Good good = new Good(null, null, null, null, null, 0,null);
		System.out.println(good);
	}*/

	@Test
	public void testHash(){
		Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(CACHEGOOD);


	}

	@Test
	public void testEl(){
		String s = "1,2,3";
		String[] split = s.split(",");
		List<String[]> strings = new ArrayList<>();


	}
	/*@Test
	public void testPut(){
		Snake snake = new Snake(10L,"test",10);
		stringRedisTemplate.opsForHash().put(RedisContent.SNAKEKEY,String.valueOf(snake.getSnakeId()), JSONUtil.toJsonStr(snake));
	}*/
	@Test
	public void testHash2(){

		String str = "10,11";
		String[] idStr = str.split(",");
		List<String> idList = new ArrayList();
		for (String s : idStr) {
			idList.add(s);
		}
		HashOperations<String, String, String> stringObjectObjectHashOperations = stringRedisTemplate.opsForHash();
		List<String> list = stringObjectObjectHashOperations.multiGet(RedisContent.SNAKEKEY, idList);
		/*for (Object o : list) {
			Snake snake = (Snake) o;
			System.out.println(snake);
		}*/

		List<Snake> collect = list.stream().map(test -> JSONUtil.toBean(test, Snake.class)).collect(Collectors.toList());
		System.out.println(list);
		System.out.println(collect);

	}

	/*@Test
	public void testBuilder(){
		List<Scale> scales = new ArrayList<>();
		scales.add(new Scale(1L,"小",new BigDecimal(10.5),1L));
		scales.add(new Scale(2L,"中",new BigDecimal(10.5),1L));
		scales.add(new Scale(3L,"大",new BigDecimal(10.5),1L));
		StringBuffer scaleBuffer = new StringBuffer();
		StringBuffer priceBuffer = new StringBuffer();
		for (Scale scale : scales) {
			scaleBuffer.append(scale.getScale()+",");
			priceBuffer.append(scale.getPrice()+",");
		}
		scaleBuffer.deleteCharAt(5);
		System.out.println(scaleBuffer);
		System.out.println(priceBuffer);
	}*/

	@Test
	public void testString(){
		List<String> o=new ArrayList<>();
		o.add("米粉");
		o.add("肠粉");
		o.add("炒粉");
		log.info("list:{}", o);
		String join = String.join(",", o);
		log.info("join:{}",join);
	}
}
