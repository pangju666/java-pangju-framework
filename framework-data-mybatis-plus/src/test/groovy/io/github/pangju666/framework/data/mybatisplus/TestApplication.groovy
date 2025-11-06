package io.github.pangju666.framework.data.mybatisplus

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@MapperScan("io.github.pangju666.framework.data.mybatisplus.mapper")
@SpringBootApplication
class TestApplication {
	static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args)
	}
}
