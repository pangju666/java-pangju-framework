package io.github.pangju666.framework.web

import org.springframework.boot.SpringBootConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestClient

@SpringBootConfiguration
class BeanConfig {
	@Bean
	RestClient restClient(RestClient.Builder builder) {
		return builder.build();
	}
}
