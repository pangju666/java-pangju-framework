package io.github.pangju666.framework.web.config

import io.github.pangju666.framework.web.filter.HttpExceptionFilter
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestClient

@SpringBootConfiguration
class BeanConfig {
	@Bean
	RestClient restClient(RestClient.Builder builder) {
		return builder.build()
	}

	@Bean
	FilterRegistrationBean<HttpExceptionFilter> httpExceptionFilterFilterRegistrationBean() {
		return new FilterRegistrationBean<HttpExceptionFilter>(new HttpExceptionFilter(
			"/exception/types", "/exception/list", "com.test"))
	}
}
