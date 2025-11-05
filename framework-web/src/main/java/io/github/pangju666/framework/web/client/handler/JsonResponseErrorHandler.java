package io.github.pangju666.framework.web.client.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.pangju666.commons.lang.utils.JsonUtils;
import io.github.pangju666.commons.lang.utils.StringUtils;
import io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceException;
import io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceTimeoutException;
import io.github.pangju666.framework.web.model.error.HttpRemoteServiceError;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * JSON响应错误处理器
 * <p>
 * 实现 {@link ResponseErrorHandler} 接口，基于远程服务返回的 JSON 响应体与
 * HTTP 状态信息判断是否发生错误，并在需要时抛出统一的远程服务异常。
 * </p>
 *
 * <p>
 * 主要特性：
 * <ul>
 *     <li>按响应头 Content-Type 判断是否为 JSON</li>
 *     <li>支持基于 HTTP 状态码或业务字段判定结果</li>
 *     <li>支持自定义业务码字段名与成功码值</li>
 *     <li>支持通过谓词自定义成功判定逻辑</li>
 *     <li>对网关超时（{@link HttpStatus#GATEWAY_TIMEOUT}）抛出专用异常</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @see ResponseErrorHandler
 * @see io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceException
 * @see io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceTimeoutException
 * @since 1.0.0
 */
public class JsonResponseErrorHandler implements ResponseErrorHandler {
	/**
	 * 远程服务名称
	 *
	 * @since 1.0.0
	 */
	private String service;
	/**
	 * API 接口名称或路径
	 *
	 * @since 1.0.0
	 */
	private String api;
	/**
	 * 响应体中的业务码字段名，默认值为 {@code code}
	 *
	 * @since 1.0.0
	 */
	private String codeField = "code";
	/**
	 * 判定成功的业务码值（可为布尔、数字或字符串）
	 *
	 * @since 1.0.0
	 */
	private Object successCode;
	/**
	 * 自定义异常消息，非空时在抛出异常时使用
	 *
	 * @since 1.0.0
	 */
	private String customExceptionMessage;
	/**
	 * 解析到的当前响应中的业务码值（布尔、数字或字符串）
	 *
	 * @since 1.0.0
	 */
	private Object errorCode;
	/**
	 * 响应体中的错误消息字段名，默认值为 {@code message}
	 *
	 * @since 1.0.0
	 */
	private String messageField = "message";
	/**
	 * 成功判定谓词，当存在时优先据此判断响应是否成功
	 *
	 * @since 1.0.0
	 */
	private Predicate<JsonObject> successPredicate;
	/**
	 * 最近一次解析到的响应体 JSON 对象缓存
	 *
	 * @since 1.0.0
	 */
	private JsonObject responseBody;

	/**
	 * 构造函数（基于成功判定谓词）
	 * <p>通过传入自定义成功判定谓词对 JSON 响应体进行判定。</p>
	 *
	 * @param successPredicate 自定义成功判定谓词，不能为空
	 * @throws IllegalArgumentException 当 {@code successPredicate} 为空时抛出
	 * @since 1.0.0
	 */
	public JsonResponseErrorHandler(Predicate<JsonObject> successPredicate) {
		Assert.notNull(successPredicate, "successPredicate 不可为空");
		this.successPredicate = successPredicate;
	}

	/**
	 * 构造函数（基于成功业务码的结果判定）
	 * <p>通过传入成功业务码与响应体中的业务码进行比对判定。</p>
	 *
	 * @param successCode 判定成功的业务码
	 * @throws IllegalArgumentException 当 {@code successCode} 为空时抛出
	 * @since 1.0.0
	 */
	public JsonResponseErrorHandler(Object successCode) {
		Assert.notNull(successCode, "successCode 不可为空");
		this.successCode = successCode;
	}

	/**
	 * 设置远程服务名称
	 *
	 * @param service 远程服务名称
	 * @since 1.0.0
	 */
	public void setService(String service) {
		this.service = service;
	}

	/**
	 * 设置 API 接口名称或路径
	 *
	 * @param api API 接口名称或路径
	 * @since 1.0.0
	 */
	public void setApi(String api) {
		this.api = api;
	}

	/**
	 * 设置自定义异常消息
	 *
	 * @param customExceptionMessage 自定义异常消息
	 * @since 1.0.0
	 */
	public void setCustomExceptionMessage(String customExceptionMessage) {
		this.customExceptionMessage = customExceptionMessage;
	}

	/**
	 * 设置业务码字段名
	 *
	 * @param codeField 业务码字段名，不能为空
	 * @throws IllegalArgumentException 当 {@code codeField} 为空时抛出
	 * @since 1.0.0
	 */
	public void setCodeField(String codeField) {
		Assert.hasText(codeField, "codeField 不可为空");
		this.codeField = codeField;
	}

	/**
	 * 设置错误消息字段名
	 *
	 * @param messageField 错误消息字段名，不能为空
	 * @throws IllegalArgumentException 当 {@code messageField} 为空时抛出
	 * @since 1.0.0
	 */
	public void setMessageField(String messageField) {
		Assert.hasText(messageField, "messageField 不可为空");
		this.messageField = messageField;
	}

	/**
	 * 判断响应是否存在错误
	 * <p>
	 * 判定流程：
	 * <ol>
	 *     <li>非 JSON 响应直接判定为无错误</li>
	 *     <li>HTTP 状态码为错误则判定为错误</li>
	 *     <li>读取响应体并解析为 JSON 对象</li>
	 *     <li>若存在业务码字段：记录其值并使用谓词或成功码值判定</li>
	 *     <li>若不存在业务码字段：存在谓词则使用谓词进行判定，否则为无错误</li>
	 * </ol>
	 * </p>
	 *
	 * @param response 客户端响应对象
	 * @return 当判定为错误时返回 {@code true}，否则返回 {@code false}
	 * @throws IOException 读取响应体时发生 IO 异常
	 * @since 1.0.0
	 */
	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		if (!MediaType.APPLICATION_JSON.includes(response.getHeaders().getContentType())) {
			return false;
		}
		if (response.getStatusCode().isError()) {
			return true;
		}

		byte[] bodyBytes = FileCopyUtils.copyToByteArray(response.getBody());
		if (bodyBytes.length == 0) {
			return false;
		}

		Charset charset = StandardCharsets.UTF_8;
		if (Objects.nonNull(response.getHeaders().getContentType())) {
			charset = ObjectUtils.getIfNull(response.getHeaders().getContentType().getCharset(), StandardCharsets.UTF_8);
		}
		String bodyStr = new String(bodyBytes, charset);

		JsonElement responseJson = JsonUtils.parseString(bodyStr);
		if (!responseJson.isJsonObject()) {
			return false;
		}

		JsonObject responseJsonObject = responseJson.getAsJsonObject();
		this.responseBody = responseJsonObject;
		if (!responseJsonObject.has(this.codeField)) {
			if (Objects.nonNull(this.successPredicate)) {
				return !this.successPredicate.test(responseJsonObject);
			}
			return false;
		}

		try {
			JsonPrimitive codeJson = responseJsonObject.getAsJsonPrimitive(this.codeField);
			if (codeJson.isBoolean()) {
				this.errorCode = codeJson.getAsBoolean();
			} else if (codeJson.isNumber()) {
				this.errorCode = codeJson.getAsNumber();
			} else if (codeJson.isString()) {
				this.errorCode = codeJson.getAsString();
			}
		} catch (ClassCastException ignored) {
		}

		if (Objects.nonNull(this.successPredicate)) {
			return !this.successPredicate.test(responseJsonObject);
		}
		return this.successCode.equals(this.errorCode);
	}

	/**
	 * 处理错误响应并抛出统一异常
	 * <p>
	 * 从响应体中提取错误消息（可选），构造 {@link HttpRemoteServiceError}。
	 * 当 HTTP 状态码为 {@link HttpStatus#GATEWAY_TIMEOUT} 时抛出
	 * {@link io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceTimeoutException}，
	 * 否则抛出 {@link io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceException}。
	 * 若设置了自定义异常消息，则优先使用自定义消息。
	 * </p>
	 *
	 * @param url      请求的完整 URI
	 * @param method   HTTP 请求方法
	 * @param response 客户端响应对象
	 * @throws IOException 读取响应体时发生 IO 异常
	 * @since 1.0.0
	 */
	@Override
	public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
		String responseErrorMessage = null;
		if (this.responseBody.has(this.messageField)) {
			try {
				JsonPrimitive messageJson = this.responseBody.getAsJsonPrimitive(this.messageField);
				responseErrorMessage = messageJson.getAsString();
			} catch (ClassCastException ignored) {
			}
		}
		HttpRemoteServiceError remoteServiceError = new HttpRemoteServiceError(this.service, this.api, url,
			responseErrorMessage, Objects.toString(this.errorCode, null),
			response.getStatusCode());

		if (response.getStatusCode().equals(HttpStatus.GATEWAY_TIMEOUT)) {
			if (StringUtils.isNotBlank(this.customExceptionMessage)) {
				throw new HttpRemoteServiceTimeoutException(this.customExceptionMessage, remoteServiceError);
			} else {
				throw new HttpRemoteServiceTimeoutException(remoteServiceError);
			}
		} else {
			if (StringUtils.isNotBlank(this.customExceptionMessage)) {
				throw new HttpRemoteServiceException(this.customExceptionMessage, remoteServiceError);
			} else {
				throw new HttpRemoteServiceException(remoteServiceError);
			}
		}
	}
}
