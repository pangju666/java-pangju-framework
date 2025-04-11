package io.github.pangju666.framework.data.mybatisplus.config;

import io.github.pangju666.framework.data.mybatisplus.injector.TableLogicFillSqlInjector;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class BeanConfig {
	@Bean
	TableLogicFillSqlInjector tableLogicFillSqlInjector() {
		return new TableLogicFillSqlInjector();
	}
}
