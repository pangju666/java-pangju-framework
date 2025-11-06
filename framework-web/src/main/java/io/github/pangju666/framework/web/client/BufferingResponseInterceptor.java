/*
 *   Copyright 2025 pangju666
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.pangju666.framework.web.client;

import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * 响应内容缓存拦截器
 * <p>
 * 通过将 {@link ClientHttpResponse} 包装为
 * {@link BufferingClientHttpResponseWrapper}，
 * 缓存响应体数据以支持对响应体的重复读取（例如日志打印、错误处理器解析等）。
 * 该拦截器本身无状态，适用于在多线程环境下复用。
 * </p>
 *
 * <p>
 * 使用场景：
 * 当后续处理（如 {@code ResponseErrorHandler} 或其他拦截器）需要多次读取响应体时，
 * 在客户端构建阶段注册本拦截器即可避免一次性流被消费后无法再次读取的问题。
 * </p>
 *
 * <p>
 * 匹配策略与性能：
 * <ul>
 *   <li>仅当响应头中的 Content-Type 属于可接受媒体类型集合（{@link #acceptableMediaTypes}）时，才进行缓冲包装；
 *       其他类型直接返回原始响应，避免不必要的内存开销。</li>
 *   <li>默认接受 {@code application/json} 与 {@code application/json;charset=UTF-8}，可通过构造函数自定义。</li>
 *   <li>缓冲包装会将响应体读入内存，以支持重复读取；对于超大响应体，请谨慎启用或限制可接受类型。</li>
 * </ul>
 * </p>
 *
 * <p>注册示例</p>
 * <pre>{@code
 * RestClient restClient = RestClient.builder()
 *     .requestInterceptors(interceptors -> interceptors.add(0, new BufferingResponseInterceptor()))
 *     .build();
 * }
 * </pre>
 *
 * @author pangju666
 * @see ClientHttpRequestInterceptor
 * @see ClientHttpResponse
 * @see BufferingClientHttpResponseWrapper
 * @see JsonResponseErrorHandler
 * @since 1.0.0
 */
public class BufferingResponseInterceptor implements ClientHttpRequestInterceptor {
	/**
	 * 可接受的媒体类型集合
	 * <p>
	 * 仅当响应的 {@code Content-Type} 属于该集合之一时，才会执行缓冲包装；
	 * 否则直接返回底层响应以减少不必要的内存开销。
	 * 默认包含 {@code application/json} 与 {@code application/json;charset=UTF-8}。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	private final Set<String> acceptableMediaTypes;

	/**
	 * 使用默认可接受媒体类型构造拦截器。
	 * <p>
	 * 默认接受 {@code application/json} 与 {@code application/json;charset=UTF-8}。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public BufferingResponseInterceptor() {
		this.acceptableMediaTypes = Set.of(MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE);
	}

	/**
	 * 使用自定义可接受媒体类型集合构造拦截器。
	 * <p>
	 * 传入集合会被包装为不可变集合（{@link Collections#unmodifiableSet(Set)}）。
	 * </p>
	 *
	 * @param acceptableMediaTypes 自定义媒体类型字符串集合，不可为 {@code null}
	 * @throws NullPointerException 当 {@code acceptableMediaTypes} 为 {@code null} 时抛出
	 * @since 1.0.0
	 */
	public BufferingResponseInterceptor(Set<String> acceptableMediaTypes) {
		this.acceptableMediaTypes = Collections.unmodifiableSet(acceptableMediaTypes);
	}

	/**
	 * 执行拦截逻辑
	 * <p>
	 * 委派请求执行并检查响应头的 Content-Type；当类型为空或不在可接受集合中时，
	 * 直接返回底层响应；当类型可接受时，使用 {@link BufferingClientHttpResponseWrapper}
	 * 包装响应以支持重复读取响应体。
	 * </p>
	 *
	 * @param request   原始请求
	 * @param body      请求体字节数组
	 * @param execution 请求执行器
	 * @return 原始响应或缓冲包装后的响应
	 * @throws IOException IO 异常
	 * @since 1.0.0
	 */
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		ClientHttpResponse response = execution.execute(request, body);
		MediaType contentType = response.getHeaders().getContentType();
		if (Objects.isNull(contentType) || !acceptableMediaTypes.contains(contentType.toString())) {
			return response;
		}
		return new BufferingClientHttpResponseWrapper(response);
	}
}
