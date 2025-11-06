/*
 *    Copyright 2025 pangju666
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package io.github.pangju666.framework.web.config

import io.github.pangju666.framework.web.client.BufferingResponseInterceptor
import io.github.pangju666.framework.web.servlet.filter.HttpExceptionInfoFilter
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.core.Ordered
import org.springframework.web.client.RestClient

@SpringBootConfiguration
class BeanConfig {
	@Bean
	RestClient restClient(RestClient.Builder builder) {
		return builder.requestInterceptor(new BufferingResponseInterceptor()).build();
	}

	@Bean
	FilterRegistrationBean<HttpExceptionInfoFilter> httpExceptionInfoFilterFilterRegistrationBean() {
		HttpExceptionInfoFilter httpExceptionInfoFilter = new HttpExceptionInfoFilter(
			"/http-exception/types", "/http-exception/list", Collections.emptyList());
		FilterRegistrationBean<HttpExceptionInfoFilter> filterRegistrationBean = new FilterRegistrationBean<>(httpExceptionInfoFilter);
		filterRegistrationBean.addUrlPatterns("/http-exception/types", "/http-exception/list")
		filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return filterRegistrationBean;
	}
}
