package com.keyuan;

import com.keyuan.utils.WebSocketServerUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class KeyuanApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(KeyuanApplication.class, args);
		WebSocketServerUtil bean = run.getBean(WebSocketServerUtil.class);
	}

}
