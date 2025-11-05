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

package io.github.pangju666.framework.web.client.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.pangju666.commons.lang.pool.Constants;
import io.github.pangju666.framework.web.client.handler.JsonResponseErrorHandler;
import io.github.pangju666.framework.web.pool.WebConstants;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.function.Predicate;

/**
 * RestClient 辅助构建器
 * <p>
 * 提供流式 API 的 HTTP 请求构建与执行能力，基于 {@link RestClient} 和
 * {@link UriComponentsBuilder} 封装常见配置：URI 构建、请求头管理、请求体设置、
 * 响应转换与可选的 JSON 错误处理集成。
 * </p>
 *
 * <p>
 * 功能特性：
 * <ul>
 *   <li>URI 构建：支持路径、查询参数、URI 变量</li>
 *   <li>请求头管理：支持单个或批量添加（数组/集合自动展开）</li>
 *   <li>请求体设置：支持 JSON、表单、文本、字节、{@link Resource 资源}</li>
 *   <li>响应转换：支持 {@code Resource}、字节、字符串、JSON、Bean 等类型</li>
 *   <li>错误处理：可集成 {@link JsonResponseErrorHandler} 进行统一异常转换</li>
 * </ul>
 * </p>
 *
 * <p>
 * 异常行为：
 * <ul>
 *   <li>未配置错误处理器时：失败由 {@link RestClient} 抛出 {@link org.springframework.web.client.RestClientResponseException}</li>
 *   <li>配置 {@link JsonResponseErrorHandler} 时：业务失败抛出
 *       {@link io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceException}；
 *       网关超时抛出
 *       {@link io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceTimeoutException}</li>
 * </ul>
 * </p>
 *
 * <p>使用示例</p>
 * <pre>{@code
 * // 示例一：GET 并按 JSON 解析
 * Result result = RestRequestBuilder.fromUriString(restClient, "https://api.example.com")
 *     .path("/api/test/{id}")
 *     .queryParam("param", 123)
 *     .uriVariable("id", 1)
 *     .toJson(Result.class);
 *
 * // 示例二：POST JSON 并按 Bean 解析
 * Result result2 = RestRequestBuilder.fromUriString(restClient, "https://api.example.com")
 *     .path("/api/test/{id}")
 *     .method(HttpMethod.POST)
 *     .header("Authorization", "Bearer token")
 *     .uriVariable("id", 1)
 *     .jsonBody(new User("admin", "password"))
 *     .toBean(Result.class, MediaType.APPLICATION_JSON);
 *
 * // 示例三：上传文件（multipart）并忽略响应体
 * RestRequestBuilder.fromUriString(restClient, "https://api.example.com")
 *     .path("/api/upload/{id}")
 *     .method(HttpMethod.POST)
 *     .uriVariable("id", 1)
 *     .formData("file", new FileSystemResource(new File("xxxx")))
 *     .toBodiless();
 *
 * // 示例四：集成 JSON 错误处理器（按业务码判定成功）
 * Result result3 = RestRequestBuilder.fromUriString(restClient, "https://api.example.com")
 *     .path("/api/test/{id}")
 *     .method(HttpMethod.POST)
 *     .withJsonErrorHandler("SUCCESS")
 *     .errorService("用户服务")
 *     .errorApi("创建用户")
 *     .jsonBody(new User("admin", "password"))
 *     .toJson(Result.class);
 * }
 * </pre>
 *
 * <p>
 * 线程安全与复用：
 * <ul>
 *   <li>本构建器为可变对象，非线程安全；建议一次请求一次实例。</li>
 *   <li>链式调用会累积状态（路径、查询参数、头、体等），请避免跨请求复用。</li>
 * </ul>
 * </p>
 *
 * <p>
 * 默认行为与边界：
 * <ul>
 *   <li>多次调用 {@code path(...)} 会按调用顺序追加路径段。</li>
 *   <li>数组/集合类型的查询参数与请求头会自动展开为多个条目；{@code null} 跳过。</li>
 *   <li>响应转换方法会在检索前设置合适的 {@code Accept} 头（例如 JSON）。</li>
 *   <li>URI 模板变量需与路径模板占位名称一致，未提供的占位符会导致构建失败。</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @see RestClient
 * @see JsonResponseErrorHandler
 * @since 1.0.0
 */
public class RestRequestBuilder {
	/**
	 * 表单媒体类型集合，包含Spring支持的表单数据类型
	 *
	 * @see FormHttpMessageConverter#getSupportedMediaTypes()
	 * @since 1.0.0
	 */
	public static final Set<MediaType> FORM_MEDIA_TYPES = Set.of(MediaType.MULTIPART_FORM_DATA,
		MediaType.MULTIPART_MIXED, MediaType.MULTIPART_RELATED);

	/**
	 * RestClient实例，用于执行HTTP请求
	 *
	 * @since 1.0.0
	 */
	protected final RestClient restClient;
	/**
	 * URI构建器，用于构建请求URI
	 *
	 * @since 1.0.0
	 */
	protected final UriComponentsBuilder uriComponentsBuilder;

	/**
	 * 请求头集合
	 *
	 * @since 1.0.0
	 */
	protected final HttpHeaders headers = new HttpHeaders();
	/**
	 * URI变量映射，用于替换URI模板中的变量
	 *
	 * @since 1.0.0
	 */
	protected final Map<String, Object> uriVariables = new HashMap<>(4);
	/**
	 * 表单数据集合，用于构建表单请求体
	 *
	 * @since 1.0.0
	 */
	protected final MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();

	/**
	 * HTTP请求方法，默认为GET
	 *
	 * @since 1.0.0
	 */
	protected HttpMethod method = HttpMethod.GET;
	/**
	 * 内容类型，默认为application/x-www-form-urlencoded
	 *
	 * @since 1.0.0
	 */
	protected MediaType contentType = MediaType.APPLICATION_FORM_URLENCODED;
	/**
	 * 请求体
	 *
	 * @since 1.0.0
	 */
	protected Object body = null;
	/**
	 * JSON 响应错误处理器，用于统一处理远程服务错误
	 *
	 * @see JsonResponseErrorHandler
	 * @since 1.0.0
	 */
	protected JsonResponseErrorHandler errorHandler;

	/**
	 * 使用RestClient实例和URI构建器构造辅助类
	 * <p>
	 * 该构造方法为protected，推荐使用静态工厂方法创建实例
	 * </p>
	 *
	 * @param restClient           RestClient实例
	 * @param uriComponentsBuilder URI构建器
	 * @since 1.0.0
	 */
	protected RestRequestBuilder(RestClient restClient, UriComponentsBuilder uriComponentsBuilder) {
		this.restClient = restClient;
		this.uriComponentsBuilder = uriComponentsBuilder;
	}

	/**
	 * 从URI字符串创建RestRequestBuilder实例
	 *
	 * @param restClient RestClient实例
	 * @param uriString  URI字符串（可选），例如：{@code "https://api.example.com/users"}
	 * @return 新的RestRequestBuilder实例
	 * @throws IllegalArgumentException 当restClient为null时抛出
	 * @since 1.0.0
	 */
	public static RestRequestBuilder fromUriString(final RestClient restClient, final String uriString) {
		Assert.notNull(restClient, "restClient 不可为null");

		if (StringUtils.isNotBlank(uriString)) {
			if (uriString.endsWith(WebConstants.HTTP_PATH_SEPARATOR)) {
				return new RestRequestBuilder(restClient, UriComponentsBuilder.fromUriString(
					uriString.substring(0, uriString.length() - 1)));
			} else {
				return new RestRequestBuilder(restClient, UriComponentsBuilder.fromUriString(uriString));
			}
		} else {
			return new RestRequestBuilder(restClient, UriComponentsBuilder.newInstance());
		}
	}

	/**
	 * 从URI对象创建RestRequestBuilder实例
	 *
	 * @param restClient RestClient实例
	 * @param uri        URI对象（可选），例如：{@code new URI("https://api.example.com/users")}
	 * @return 新的RestRequestBuilder实例
	 * @throws IllegalArgumentException 当restClient为null时抛出
	 * @since 1.0.0
	 */
	public static RestRequestBuilder fromUri(final RestClient restClient, final URI uri) {
		Assert.notNull(restClient, "restClient 不可为null");

		if (Objects.nonNull(uri)) {
			return new RestRequestBuilder(restClient, UriComponentsBuilder.fromUri(uri));
		} else {
			return new RestRequestBuilder(restClient, UriComponentsBuilder.newInstance());
		}
	}

	/**
	 * 配置 JSON 错误处理器（基于成功判定谓词）
	 * <p>通过谓词判定响应是否成功。</p>
	 *
	 * @param successPredicate 成功判定谓词，不能为空
	 * @return 当前实例
	 * @since 1.0.0
	 */
	public RestRequestBuilder withJsonErrorHandler(Predicate<JsonObject> successPredicate) {
		this.errorHandler = new JsonResponseErrorHandler(successPredicate);
		return this;
	}

	/**
	 * 配置 JSON 错误处理器（基于成功业务码值）
	 * <p>将响应中的业务码与指定成功码值进行比对判定。</p>
	 *
	 * @param successCodeValue 判定成功的业务码值，不能为空
	 * @return 当前实例
	 * @since 1.0.0
	 */
	public RestRequestBuilder withJsonErrorHandler(Object successCodeValue) {
		this.errorHandler = new JsonResponseErrorHandler(successCodeValue);
		return this;
	}

	/**
	 * 配置 JSON 错误处理器（自定义实例）
	 *
	 * @param errorHandler 自定义错误处理器实例
	 * @return 当前实例
	 * @since 1.0.0
	 */
	public RestRequestBuilder withJsonErrorHandler(JsonResponseErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
		return this;
	}

	/**
	 * 设置错误信息中的远程服务名称
	 * <p>仅在已配置JSON响应错误处理器时有效，未配置将被忽略。</p>
	 *
	 * @param service 远程服务名称
	 * @return 当前实例
	 * @see #withJsonErrorHandler
	 * @since 1.0.0
	 */
	public RestRequestBuilder errorService(String service) {
		if (Objects.nonNull(this.errorHandler)) {
			this.errorHandler.setService(service);
		}
		return this;
	}

	/**
	 * 设置错误信息中的 API 接口名称
	 * <p>仅在已配置JSON响应错误处理器时有效，未配置将被忽略。</p>
	 *
	 * @param api API 接口名称或路径
	 * @return 当前实例
	 * @see #withJsonErrorHandler
	 * @since 1.0.0
	 */
	public RestRequestBuilder errorApi(String api) {
		if (Objects.nonNull(this.errorHandler)) {
			this.errorHandler.setApi(api);
		}
		return this;
	}

	/**
	 * 设置异常消息
	 * <p>当抛出远程服务异常时优先使用该消息。</p>
	 * <p>仅在已配置JSON响应错误处理器时有效，未配置将被忽略。</p>
	 *
	 * @param exceptionMessage 自定义异常消息
	 * @return 当前实例
	 * @see #withJsonErrorHandler
	 * @since 1.0.0
	 */
	public RestRequestBuilder exceptionMessage(String exceptionMessage) {
		if (Objects.nonNull(this.errorHandler)) {
			this.errorHandler.setCustomExceptionMessage(exceptionMessage);
		}
		return this;
	}

	/**
	 * 设置响应体中的业务码字段名
	 * <p>默认字段名为 {@code code}。</p>
	 * <p>仅在已配置JSON响应错误处理器时有效，未配置将被忽略。</p>
	 *
	 * @param codeMemberName 业务码字段名，不能为空
	 * @return 当前实例
	 * @see #withJsonErrorHandler
	 * @since 1.0.0
	 */
	public RestRequestBuilder errorCodeField(String codeMemberName) {
		if (Objects.nonNull(this.errorHandler)) {
			this.errorHandler.setCodeField(codeMemberName);
		}
		return this;
	}

	/**
	 * 设置响应体中的错误消息字段名
	 * <p>默认字段名为 {@code message}。</p>
	 * <p>仅在已配置JSON响应错误处理器时有效，未配置将被忽略。</p>
	 *
	 * @param messageMemberName 错误消息字段名，不能为空
	 * @return 当前实例
	 * @see #withJsonErrorHandler
	 * @since 1.0.0
	 */
	public RestRequestBuilder errorMessageField(String messageMemberName) {
		if (Objects.nonNull(this.errorHandler)) {
			this.errorHandler.setMessageField(messageMemberName);
		}
		return this;
	}

	/**
	 * 设置HTTP请求方法
	 *
	 * @param method HTTP方法，例如：{@code HttpMethod.POST}
	 * @return 当前实例
	 * @since 1.0.0
	 */
	public RestRequestBuilder method(HttpMethod method) {
		if (Objects.nonNull(method)) {
			this.method = method;
		}
		return this;
	}

	/**
	 * 添加请求路径（规范化前导斜杠，支持多次追加）
	 * <p>
	 * 行为说明：
	 * <ul>
	 *   <li>当传入的 {@code path} 不以 {@code '/'} 开头时，自动在前面补一个 {@code '/'}</li>
	 *   <li>多次调用会按顺序追加路径段，不会清空已设置的路径</li>
	 *   <li>支持包含模板占位符（例如：{@code "/api/users/{id}"}），可与 {@link #uriVariable(String, Object)} 配合使用</li>
	 *   <li>传入空白字符串（仅空格/空）时不做任何处理</li>
	 * </ul>
	 * </p>
	 *
	 * @param path 请求路径，例如：{@code "/api/users"}
	 * @return 当前实例
	 * @see UriComponentsBuilder#path(String)
	 * @since 1.0.0
	 */
	public RestRequestBuilder path(String path) {
		if (StringUtils.isNotBlank(path)) {
			this.uriComponentsBuilder.path(path.startsWith(WebConstants.HTTP_PATH_SEPARATOR) ? path :
				WebConstants.HTTP_PATH_SEPARATOR + path);
		}
		return this;
	}

	/**
	 * 添加单个查询参数（支持多值）
	 * <p>
	 * 传入多个 {@code values} 时按顺序添加；若某个值为 {@code null}，将按
	 * {@link UriComponentsBuilder#queryParam(String, Object...)} 的默认规则处理（通常生成无值参数）。
	 * </p>
	 *
	 * @param name   参数名，例如：{@code "page"}
	 * @param values 参数值序列，例如：{@code 1, 2, 3}
	 * @return 当前实例
	 * @throws IllegalArgumentException 当name为空时抛出
	 * @see UriComponentsBuilder#queryParam(String, Object...)
	 * @since 1.0.0
	 */
	public RestRequestBuilder queryParam(String name, @Nullable Object... values) {
		Assert.hasText(name, "name 不可为空");

		this.uriComponentsBuilder.queryParam(name, values);
		return this;
	}

	/**
	 * 批量添加查询参数（MultiValueMap 合并）
	 * <p>
	 * 将提供的查询参数映射合并到当前 URI 构建器中；当 {@code params} 为 {@code null}
	 * 或空时不做任何处理。
	 * </p>
	 *
	 * @param params 参数映射，例如：{@code new LinkedMultiValueMap<>()}
	 * @return 当前实例
	 * @see UriComponentsBuilder#queryParams(MultiValueMap)
	 * @since 1.0.0
	 */
	public RestRequestBuilder queryParams(@Nullable MultiValueMap<String, String> params) {
		this.uriComponentsBuilder.queryParams(params);
		return this;
	}

	/**
	 * 批量添加查询参数（Map，支持数组/集合/Optional）
	 * <p>
	 * 行为说明：
	 * <ul>
	 *   <li>数组：自动展开为多个同名参数</li>
	 *   <li>集合：自动展开为多个同名参数</li>
	 *   <li>Optional：使用 {@link UriComponentsBuilder#queryParamIfPresent(String, Optional)}，仅在值存在时添加</li>
	 *   <li>其他类型：按单值添加；当值为 {@code null} 时传递至 {@code queryParam}，由框架按默认规则处理（通常生成无值参数）</li>
	 * </ul>
	 * 当 {@code params} 为 {@code null} 或空时，不做任何处理。
	 * </p>
	 *
	 * @param params 查询参数映射，key 为参数名，value 支持数组、集合、Optional、普通对象
	 * @return 当前实例
	 * @since 1.0.0
	 */
	public RestRequestBuilder queryParams(@Nullable Map<String, Object> params) {
		if (!CollectionUtils.isEmpty(params)) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				if (Objects.nonNull(entry.getValue()) && entry.getValue().getClass().isArray()) {
					this.uriComponentsBuilder.queryParam(entry.getKey(), (Object[]) entry.getValue());
				} else if (Objects.nonNull(entry.getValue()) && entry.getValue() instanceof Collection<?> collection) {
					this.uriComponentsBuilder.queryParam(entry.getKey(), collection);
				} else if (Objects.nonNull(entry.getValue()) && entry.getValue() instanceof Optional<?> optional) {
					this.uriComponentsBuilder.queryParamIfPresent(entry.getKey(), optional);
				} else {
					this.uriComponentsBuilder.queryParam(entry.getKey(), entry.getValue());
				}
			}
		}
		return this;
	}

	/**
	 * 设置原始查询字符串（自动去除前导 '?'})
	 *
	 * @param query 查询字符串，例如：{@code "page=1&size=10&sort=name,asc"}
	 * @return 当前实例
	 * @see UriComponentsBuilder#query(String)
	 * @since 1.0.0
	 */
	public RestRequestBuilder query(@Nullable String query) {
		this.uriComponentsBuilder.query(Strings.CS.startsWith(query, "?") ?
			StringUtils.substring(query, 1) : query);
		return this;
	}

	/**
	 * 添加单个 URI 模板变量（{@code null} 跳过）
	 *
	 * @param name  变量名，例如：{@code "id"}
	 * @param value 变量值，例如：{@code 123}；当为 {@code null} 时不添加
	 * @return 当前实例
	 * @throws IllegalArgumentException 当name为空时抛出
	 * @since 1.0.0
	 */
	public RestRequestBuilder uriVariable(String name, @Nullable Object value) {
		Assert.hasText(name, "name 不可为空");

		if (Objects.nonNull(value)) {
			this.uriVariables.put(name, value);
		}
		return this;
	}

	/**
	 * 批量添加 URI 模板变量（{@code null}/空不处理）
	 *
	 * @param uriVariables URI 变量映射，例如：{@code Map.of("id", 123, "name", "test")}
	 * @return 当前实例
	 * @since 1.0.0
	 */
	public RestRequestBuilder uriVariables(@Nullable Map<String, Object> uriVariables) {
		if (!CollectionUtils.isEmpty(uriVariables)) {
			this.uriVariables.putAll(uriVariables);
		}
		return this;
	}

	/**
	 * 添加单个请求头（支持数组/集合展开）
	 * <p>
	 * 根据传入的 {@code value} 类型执行不同处理：
	 * <ul>
	 *   <li>数组类型：展开为多个同名请求头，依次添加</li>
	 *   <li>集合类型：展开为多个同名请求头，依次添加</li>
	 *   <li>其他类型：按单值请求头添加</li>
	 * </ul>
	 * 若 {@code value} 为 {@code null}，则不做任何处理。
	 * 所有值通过 {@link Objects#toString(Object, String)} 转为字符串后再添加。
	 * </p>
	 *
	 * @param key   请求头名称，例如：{@code "Authorization"}
	 * @param value 请求头值/集合/数组，例如：{@code "Bearer token123"} 或 {@code List.of("a","b")}
	 * @return 当前实例
	 * @throws IllegalArgumentException 当key为空时抛出
	 * @see HttpHeaders#add(String, String)
	 * @see HttpHeaders#addAll(String, java.util.List)
	 * @see Objects#toString(Object, String)
	 * @since 1.0.0
	 */
	public RestRequestBuilder header(String key, @Nullable Object value) {
		Assert.hasText(key, "key 不可为空");

		if (Objects.nonNull(value)) {
			if (value.getClass().isArray()) {
				this.headers.addAll(key, Arrays.stream((Object[]) value)
					.map(item -> Objects.toString(item, null))
					.toList());
			} else if (value instanceof Collection<?> collection) {
				this.headers.addAll(key, collection.stream()
					.map(item -> Objects.toString(item, null))
					.toList());
			} else {
				this.headers.add(key, Objects.toString(value, null));
			}
		}
		return this;
	}

	/**
	 * 批量添加请求头（MultiValueMap）
	 * <p>
	 * 将提供的请求头映射合并到当前请求头中；当 {@code headers} 为 {@code null} 或空时，不做任何处理。
	 * </p>
	 *
	 * @param headers 请求头映射，例如：{@code new HttpHeaders()}
	 * @return 当前实例
	 * @see HttpHeaders#addAll(MultiValueMap)
	 * @since 1.0.0
	 */
	public RestRequestBuilder headers(@Nullable MultiValueMap<String, String> headers) {
		if (!CollectionUtils.isEmpty(headers)) {
			this.headers.addAll(headers);
		}
		return this;
	}

	/**
	 * 批量添加请求头（Map，支持数组/集合展开）
	 * <p>
	 * 遍历提供的请求头映射并委托至 {@link #header(String, Object)}；
	 * 支持数组/集合值展开为多个同名请求头，{@code null} 值将被跳过。
	 * </p>
	 *
	 * @param headers 请求头映射，例如：{@code Map.of("Authorization", "Bearer token123", "Accept", "application/json")}
	 * @return 当前实例
	 * @see #header(String, Object)
	 * @since 1.0.0
	 */
	public RestRequestBuilder headers(@Nullable Map<String, Object> headers) {
		if (!CollectionUtils.isEmpty(headers)) {
			for (Map.Entry<String, Object> entry : headers.entrySet()) {
				header(entry.getKey(), entry.getValue());
			}
		}
		return this;
	}

	/**
	 * 添加资源类型的表单部分
	 *
	 * @param name  表单字段名，例如：{@code "file"}
	 * @param value 资源值，例如：{@code new FileSystemResource(new File("example.txt"))}
	 * @return 当前实例
	 * @throws IllegalArgumentException 当name为空时抛出
	 * @see org.springframework.http.converter.FormHttpMessageConverter
	 * @since 1.0.0
	 */
	public RestRequestBuilder formPart(String name, @Nullable Resource value) {
		Assert.hasText(name, "name 不可为空");

		this.contentType = MediaType.MULTIPART_FORM_DATA;
		this.formData.add(name, value);
		return this;
	}

	/**
	 * 添加表单字段
	 *
	 * @param name  表单字段名，例如：{@code "username"}
	 * @param value 字段值，例如：{@code "admin"}
	 * @return 当前实例
	 * @throws IllegalArgumentException 当name为空时抛出
	 * @see org.springframework.http.converter.FormHttpMessageConverter
	 * @since 1.0.0
	 */
	public RestRequestBuilder formData(String name, @Nullable Object value) {
		Assert.hasText(name, "name 不可为空");

		this.contentType = MediaType.MULTIPART_FORM_DATA;
		this.formData.add(name, value);
		return this;
	}

	/**
	 * 批量添加表单数据
	 *
	 * @param formData 表单数据映射，例如：{@code new LinkedMultiValueMap<>()}
	 * @return 当前实例
	 * @see org.springframework.http.converter.FormHttpMessageConverter
	 * @since 1.0.0
	 */
	public RestRequestBuilder formData(@Nullable MultiValueMap<String, Object> formData) {
		this.contentType = MediaType.MULTIPART_FORM_DATA;
		if (!CollectionUtils.isEmpty(formData)) {
			this.formData.addAll(formData);
		}
		return this;
	}

	/**
	 * 设置JSON请求体
	 * <p>
	 * 当body为null时，默认使用空JSON对象
	 * </p>
	 *
	 * @param body 请求体对象，例如：{@code new User("admin", "password")}
	 * @return 当前实例
	 * @see org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
	 * @since 1.0.0
	 */
	public RestRequestBuilder jsonBody(@Nullable Object body) {
		return jsonBody(body, true);
	}

	/**
	 * 设置JSON请求体，可控制null值处理
	 *
	 * @param body        请求体对象，例如：{@code new User("admin", "password")}
	 * @param emptyIfNull 当body为null时是否使用空JSON对象，例如：{@code true}
	 * @return 当前实例
	 * @see org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
	 * @since 1.0.0
	 */
	public RestRequestBuilder jsonBody(@Nullable Object body, boolean emptyIfNull) {
		this.contentType = MediaType.APPLICATION_JSON;
		if (Objects.isNull(body)) {
			this.body = emptyIfNull ? Constants.EMPTY_JSON_OBJECT_STR : null;
		} else {
			if (body instanceof JsonElement jsonElement) {
				this.body = jsonElement.toString();
			} else {
				this.body = body;
			}
		}
		return this;
	}

	/**
	 * 设置文本请求体
	 * <p>
	 * 当body为null时，默认使用空字符串
	 * </p>
	 *
	 * @param body 文本内容，例如：{@code "Hello, World!"}
	 * @return 当前实例
	 * @see org.springframework.http.converter.StringHttpMessageConverter
	 * @since 1.0.0
	 */
	public RestRequestBuilder textBody(@Nullable String body) {
		return textBody(body, true);
	}

	/**
	 * 设置文本请求体，可控制null值处理
	 *
	 * @param body        文本内容，例如：{@code "Hello, World!"}
	 * @param emptyIfNull 当body为null时是否使用空字符串，例如：{@code true}
	 * @return 当前实例
	 * @see org.springframework.http.converter.StringHttpMessageConverter
	 * @since 1.0.0
	 */
	public RestRequestBuilder textBody(@Nullable String body, boolean emptyIfNull) {
		this.contentType = MediaType.TEXT_PLAIN;
		this.body = ObjectUtils.getIfNull(body, emptyIfNull ? StringUtils.EMPTY : null);
		return this;
	}

	/**
	 * 设置二进制请求体
	 * <p>
	 * 当body为null时，默认使用空字节数组
	 * </p>
	 *
	 * @param body 二进制数据，例如：{@code Files.readAllBytes(Paths.get("example.bin"))}
	 * @return 当前实例
	 * @see org.springframework.http.converter.ByteArrayHttpMessageConverter
	 * @since 1.0.0
	 */
	public RestRequestBuilder bytesBody(@Nullable byte[] body) {
		return bytesBody(body, true);
	}

	/**
	 * 设置二进制请求体，可控制null值处理
	 *
	 * @param body        二进制数据，例如：{@code Files.readAllBytes(Paths.get("example.bin"))}
	 * @param emptyIfNull 当body为null时是否使用空字节数组，例如：{@code true}
	 * @return 当前实例
	 * @see org.springframework.http.converter.ByteArrayHttpMessageConverter
	 * @since 1.0.0
	 */
	public RestRequestBuilder bytesBody(@Nullable byte[] body, boolean emptyIfNull) {
		this.contentType = MediaType.APPLICATION_OCTET_STREAM;
		this.body = ObjectUtils.getIfNull(body, emptyIfNull ? ArrayUtils.EMPTY_BYTE_ARRAY : null);
		return this;
	}

	/**
	 * 设置资源请求体
	 *
	 * @param body 资源对象，例如：{@code new FileSystemResource(new File("example.txt"))}
	 * @return 当前实例
	 * @see org.springframework.http.converter.ResourceHttpMessageConverter
	 * @since 1.0.0
	 */
	public RestRequestBuilder resourceBody(@Nullable Resource body) {
		this.contentType = MediaType.APPLICATION_OCTET_STREAM;
		this.body = body;
		return this;
	}

	/**
	 * 设置请求体，指定媒体类型
	 *
	 * @param body      请求体对象，例如：{@code new User("admin", "password")}
	 * @param mediaType 媒体类型，例如：{@code MediaType.APPLICATION_JSON}
	 * @return 当前实例
	 * @throws IllegalArgumentException 当mediaType为null时抛出
	 * @see org.springframework.http.converter.HttpMessageConverter
	 * @since 1.0.0
	 */
	public RestRequestBuilder body(@Nullable Object body, MediaType mediaType) {
		Assert.notNull(mediaType, "mediaType 不可为null");

		this.contentType = mediaType;
		this.body = body;
		return this;
	}

	/**
	 * 将请求结果转换为Resource响应实体，可以指定可接受的媒体类型
	 *
	 * @param acceptableMediaTypes 可接受的媒体类型数组，例如：{@code MediaType.APPLICATION_OCTET_STREAM}
	 * @return Resource响应实体
	 * @throws RestClientResponseException                                                          当请求失败且未配置错误处理器，或错误处理器未接管时抛出
	 * @throws io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceException        当配置了错误处理器且判定为业务错误时抛出
	 * @throws io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceTimeoutException 当配置了错误处理器且判定为超时错误时抛出
	 * @see org.springframework.core.io.Resource
	 * @see org.springframework.http.converter.ResourceHttpMessageConverter
	 * @see io.github.pangju666.framework.web.client.handler.JsonResponseErrorHandler
	 * @since 1.0.0
	 */
	public ResponseEntity<Resource> toResourceEntity(final MediaType... acceptableMediaTypes) throws RestClientResponseException {
		return buildResponseSpec(acceptableMediaTypes).toEntity(Resource.class);
	}

	/**
	 * 将请求结果转换为字节数组响应实体，可以指定可接受的媒体类型
	 *
	 * @param acceptableMediaTypes 可接受的媒体类型数组，例如：{@code MediaType.APPLICATION_OCTET_STREAM}
	 * @return 字节数组响应实体
	 * @throws RestClientResponseException                                                          当请求失败且未配置错误处理器，或错误处理器未接管时抛出
	 * @throws io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceException        当配置了错误处理器且判定为业务错误时抛出
	 * @throws io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceTimeoutException 当配置了错误处理器且判定为超时错误时抛出
	 * @see org.springframework.http.converter.ByteArrayHttpMessageConverter
	 * @see io.github.pangju666.framework.web.client.handler.JsonResponseErrorHandler
	 * @since 1.0.0
	 */
	public byte[] toBytes(final MediaType... acceptableMediaTypes) throws RestClientResponseException {
		return buildResponseSpec(acceptableMediaTypes).body(byte[].class);
	}

	/**
	 * 将请求结果转换为字符串响应实体，可以指定可接受的媒体类型
	 *
	 * @param acceptableMediaTypes 可接受的媒体类型数组，例如：{@code MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON}
	 * @return 字符串响应实体
	 * @throws RestClientResponseException                                                          当请求失败且未配置错误处理器，或错误处理器未接管时抛出
	 * @throws io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceException        当配置了错误处理器且判定为业务错误时抛出
	 * @throws io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceTimeoutException 当配置了错误处理器且判定为超时错误时抛出
	 * @see org.springframework.http.converter.StringHttpMessageConverter
	 * @see io.github.pangju666.framework.web.client.handler.JsonResponseErrorHandler
	 * @since 1.0.0
	 */
	public String toString(final MediaType... acceptableMediaTypes) throws RestClientResponseException {
		return buildResponseSpec(acceptableMediaTypes).body(String.class);
	}

	/**
	 * 将请求结果转换为指定类型的JSON响应实体
	 * <p>
	 * 自动设置Accept头为{@code application/json}
	 * </p>
	 *
	 * @param bodyType 响应体类型，例如：{@code User.class}
	 * @param <T>      响应体类型
	 * @return 指定类型的JSON响应实体
	 * @throws RestClientResponseException                                                          当请求失败且未配置错误处理器，或错误处理器未接管时抛出
	 * @throws io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceException        当配置了错误处理器且判定为业务错误时抛出
	 * @throws io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceTimeoutException 当配置了错误处理器且判定为超时错误时抛出
	 * @throws IllegalArgumentException                                                             当bodyType为null时抛出
	 * @see org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
	 * @see io.github.pangju666.framework.web.client.handler.JsonResponseErrorHandler
	 * @since 1.0.0
	 */
	public <T> T toJson(Class<T> bodyType) throws RestClientResponseException {
		Assert.notNull(bodyType, "bodyType 不可为null");
		return buildResponseSpec(MediaType.APPLICATION_JSON).body(bodyType);
	}

	/**
	 * 将请求结果转换为指定泛型类型的JSON响应实体
	 * <p>
	 * 自动设置Accept头为{@code application/json}，适用于需要处理泛型的场景，如列表或嵌套对象
	 * </p>
	 *
	 * @param bodyType 响应体泛型类型，例如：{@code new ParameterizedTypeReference<List<User>>(){}}
	 * @param <T>      响应体类型
	 * @return 指定泛型类型的JSON响应实体
	 * @throws RestClientResponseException                                                          当请求失败且未配置错误处理器，或错误处理器未接管时抛出
	 * @throws io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceException        当配置了错误处理器且判定为业务错误时抛出
	 * @throws io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceTimeoutException 当配置了错误处理器且判定为超时错误时抛出
	 * @throws IllegalArgumentException                                                             当bodyType为null时抛出
	 * @see org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
	 * @see io.github.pangju666.framework.web.client.handler.JsonResponseErrorHandler
	 * @since 1.0.0
	 */
	public <T> T toJson(ParameterizedTypeReference<T> bodyType) throws RestClientResponseException {
		Assert.notNull(bodyType, "bodyType 不可为null");
		return buildResponseSpec(MediaType.APPLICATION_JSON).body(bodyType);
	}

	/**
	 * 将请求结果转换为指定类型的响应实体，可以指定可接受的媒体类型
	 *
	 * @param bodyType             响应体类型，例如：{@code User.class}
	 * @param acceptableMediaTypes 可接受的媒体类型数组，例如：{@code MediaType.APPLICATION_JSON}
	 * @param <T>                  响应体类型
	 * @return 指定类型的响应实体
	 * @throws RestClientResponseException                                                          当请求失败且未配置错误处理器，或错误处理器未接管时抛出
	 * @throws io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceException        当配置了错误处理器且判定为业务错误时抛出
	 * @throws io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceTimeoutException 当配置了错误处理器且判定为超时错误时抛出
	 * @throws IllegalArgumentException                                                             当bodyType为null时抛出
	 * @see io.github.pangju666.framework.web.client.handler.JsonResponseErrorHandler
	 * @since 1.0.0
	 */
	public <T> T toBean(Class<T> bodyType, MediaType... acceptableMediaTypes) throws RestClientResponseException {
		Assert.notNull(bodyType, "bodyType 不可为null");
		return buildResponseSpec(acceptableMediaTypes).body(bodyType);
	}

	/**
	 * 将请求结果转换为指定泛型类型的响应实体，可以指定可接受的媒体类型
	 *
	 * @param bodyType             响应体泛型类型，例如：{@code new ParameterizedTypeReference<List<User>>(){}}
	 * @param acceptableMediaTypes 可接受的媒体类型数组，例如：{@code MediaType.APPLICATION_JSON}
	 * @param <T>                  响应体类型
	 * @return 指定泛型类型的响应实体
	 * @throws RestClientResponseException                                                          当请求失败且未配置错误处理器，或错误处理器未接管时抛出
	 * @throws io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceException        当配置了错误处理器且判定为业务错误时抛出
	 * @throws io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceTimeoutException 当配置了错误处理器且判定为超时错误时抛出
	 * @throws IllegalArgumentException                                                             当bodyType为null时抛出
	 * @see io.github.pangju666.framework.web.client.handler.JsonResponseErrorHandler
	 * @since 1.0.0
	 */
	public <T> T toBean(ParameterizedTypeReference<T> bodyType, MediaType... acceptableMediaTypes) throws RestClientResponseException {
		Assert.notNull(bodyType, "bodyType 不可为null");
		return buildResponseSpec(acceptableMediaTypes).body(bodyType);
	}

	/**
	 * 将请求结果转换为无响应体的响应实体
	 *
	 * @throws RestClientResponseException                                                          当请求失败且未配置错误处理器，或错误处理器未接管时抛出
	 * @throws io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceException        当配置了错误处理器且判定为业务错误时抛出
	 * @throws io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceTimeoutException 当配置了错误处理器且判定为超时错误时抛出
	 * @see io.github.pangju666.framework.web.client.handler.JsonResponseErrorHandler
	 * @since 1.0.0
	 */
	public ResponseEntity<Void> toBodilessEntity() throws RestClientResponseException {
		return buildResponseSpec().toBodilessEntity();
	}

	/**
	 * 构建请求
	 * <p>
	 * 根据当前配置构建完整的请求规范，包括：
	 * <ul>
	 *     <li>设置请求方法和URI</li>
	 *     <li>配置请求头</li>
	 *     <li>处理请求体（根据内容类型区分表单数据和其他类型）</li>
	 * </ul>
	 * </p>
	 *
	 * @return 构建的请求规范
	 * @throws IllegalArgumentException 当请求uri为空时抛出
	 * @since 1.0.0
	 */
	public RestClient.RequestBodySpec buildRequestBodySpec() {
		URI uri = uriComponentsBuilder.build(uriVariables);
		if (StringUtils.isBlank(uri.toString())) {
			throw new IllegalArgumentException("uri 不可为空");
		}

		RestClient.RequestBodySpec requestBodySpec = restClient
			.method(method)
			.uri(uri)
			.contentType(contentType)
			.headers(httpHeaders -> httpHeaders.addAll(headers));

		if (!MediaType.APPLICATION_FORM_URLENCODED.equals(contentType)) {
			if (FORM_MEDIA_TYPES.contains(contentType)) {
				requestBodySpec.body(formData);
			} else {
				requestBodySpec.body(body);
			}
		}

		return requestBodySpec;
	}

	/**
	 * 构建响应规范
	 * <p>
	 * 在当前请求配置基础上创建{@link RestClient.ResponseSpec}，并按需设置Accept头；
	 * 若已配置{@link #errorHandler}，则将其注册到响应处理流程，用于统一判断并抛出业务异常。
	 * </p>
	 *
	 * @param acceptableMediaTypes 可接受的媒体类型列表，用于设置Accept头，例如：{@code MediaType.APPLICATION_JSON}
	 * @return 构建完成的响应规范，可继续调用{@code body(..)}、{@code toEntity(..)}或{@code toBodilessEntity()}
	 * @see #buildRequestBodySpec()
	 * @see JsonResponseErrorHandler
	 * @since 1.0.0
	 */
	protected RestClient.ResponseSpec buildResponseSpec(MediaType... acceptableMediaTypes) {
		RestClient.ResponseSpec responseSpec = buildRequestBodySpec()
			.accept(acceptableMediaTypes)
			.retrieve();
		return Objects.nonNull(this.errorHandler) ? responseSpec.onStatus(errorHandler) : responseSpec;
	}
}