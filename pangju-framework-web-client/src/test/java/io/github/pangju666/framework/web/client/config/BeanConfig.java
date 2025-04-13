package io.github.pangju666.framework.web.client.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootConfiguration
public class BeanConfig {
	@Bean
	public RestClient restClient(RestClient.Builder builder) {
		return builder.build();
	}
}
