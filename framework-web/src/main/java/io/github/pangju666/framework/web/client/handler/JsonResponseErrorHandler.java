package io.github.pangju666.framework.web.client.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.pangju666.commons.lang.utils.JsonUtils;
import io.github.pangju666.commons.lang.utils.StringUtils;
import io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceException;
import io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceTimeoutException;
import io.github.pangju666.framework.web.model.error.HttpRemoteServiceError;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * JSON 响应错误处理器
 * <p>
 * 实现 {@link ResponseErrorHandler}，根据远程服务返回的 JSON 响应体与 HTTP 状态
 * 联合判定是否存在错误，并在错误发生时抛出统一的远程服务异常。
 * </p>
 *
 * <p>
 * 特性与行为：
 * <ul>
 *   <li>按响应头 {@code Content-Type} 判断是否为 JSON；非 JSON 不进行业务判定</li>
 *   <li>对错误状态码（4xx/5xx）直接判定为错误并抛出异常</li>
 *   <li>解析响应体时遵循内容类型声明的字符集，缺省使用 UTF-8</li>
 *   <li>支持通过谓词（{@link #successPredicate}）或“成功码”（{@link #successCode}）进行业务判定</li>
 *   <li>可自定义业务码字段名与错误消息字段名（默认：{@code code}/{@code message}）</li>
 *   <li>网关超时（{@link HttpStatus#GATEWAY_TIMEOUT}）映射为 {@link HttpRemoteServiceTimeoutException}</li>
 *   <li>其他错误映射为 {@link HttpRemoteServiceException}</li>
 *   <li>当设置了自定义异常消息（{@link #customExceptionMessage}）时，优先使用该消息</li>
 * </ul>
 * </p>
 *
 * <p>
 * 线程安全：在未初始化（未锁定）阶段，该类持有可变配置（服务名、接口名、字段名、成功码/谓词、自定义异常消息），不保证线程安全。
 * 调用 {@link #init()} 后会置位 {@code initStatus} 并锁定所有配置，运行期仅进行只读访问，因此在并发场景下是安全的；
 * 首次执行 {@link #hasError(ClientHttpResponse)} 也会自动完成锁定。推荐在应用初始化阶段一次性完成配置并显式调用 {@link #init()}，
 * 随后复用同一实例以处理多线程请求。若需隔离不同配置，可在每个 {@link RestClient} 实例中分别持有处理器以实现配置隔离。
 * </p>
 *
 * @author pangju666
 * @see ResponseErrorHandler
 * @see RestClient
 * @see HttpRemoteServiceError
 * @see HttpRemoteServiceException
 * @see HttpRemoteServiceTimeoutException
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
	 * API 接口名称或路径（便于错误来源定位与日志追踪）
	 *
	 * @since 1.0.0
	 */
	private String api;
	/**
	 * 自定义异常消息，非空时在抛出异常时使用；覆盖远程响应中的错误消息
	 *
	 * @since 1.0.0
	 */
	private String customExceptionMessage;
	/**
	 * 响应体中的业务码字段名，默认 {@code code}
	 *
	 * @since 1.0.0
	 */
	private String codeField = "code";
	/**
	 * 响应体中的错误消息字段名，默认 {@code message}
	 *
	 * @since 1.0.0
	 */
	private String messageField = "message";
	/**
	 * 判定“成功”的业务码值（字符串，已归一化）
	 * <p>
	 * 通过构造器将布尔、数字与其他类型归一化为字符串，确保与响应体中的业务码进行稳定比较。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	private String successCode;
	/**
	 * 成功判定谓词，当存在时优先据此判断响应是否成功
	 *
	 * @since 1.0.0
	 */
	private Predicate<JsonObject> successPredicate;

	/**
	 * 初始化状态开关（锁定配置）
	 * <p>
	 * 默认 {@code false} 表示可修改配置；当设为 {@code true} 后，所有 setter 不再生效。
	 * 可通过显式调用 {@link #init()} 或首次调用 {@link #hasError(ClientHttpResponse)} 进行锁定。
	 * </p>
	 */
	private boolean initStatus = false;

	/**
	 * 构造函数（基于成功判定谓词）
	 * <p>通过传入自定义成功判定谓词对 JSON 响应体进行业务成功/失败判定。</p>
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
	 * 构造函数（基于“成功码”的结果判定，含归一化处理）
	 * <p>
	 * 传入的 {@code successCode} 会被归一化为字符串以便与响应体中的业务码进行稳定比较：
	 * <ul>
	 *   <li>布尔：{@code true} → {@code "true"}，{@code false} → {@code "false"}</li>
	 *   <li>数值：转换为其 {@code int} 值的字符串表示</li>
	 *   <li>字符串：原样使用</li>
	 *   <li>其他类型：使用 {@link java.util.Objects#toString(Object, String)} 转为字符串</li>
	 * </ul>
	 * 该归一化与错误码提取逻辑保持一致，确保比较语义一致性。
	 * </p>
	 *
	 * @param successCode 判定“成功”的业务码，不可为空
	 * @throws IllegalArgumentException 当 {@code successCode} 为空时抛出
	 * @since 1.0.0
	 */
	public JsonResponseErrorHandler(Object successCode) {
		Assert.notNull(successCode, "successCode 不可为空");
		if (successCode instanceof Boolean bool) {
			this.successCode = BooleanUtils.toStringTrueFalse(bool);
		} else if (successCode instanceof Number number) {
			this.successCode = String.valueOf(number.intValue());
		} else if (successCode instanceof String string) {
			this.successCode = string;
		} else {
			this.successCode = Objects.toString(successCode);
		}
	}

	/**
	 * 设置远程服务名称
	 * <p>仅在未初始化状态下生效（{@code initStatus == false}）。</p>
	 *
	 * @param service 远程服务名称
	 * @since 1.0.0
	 */
	public void setService(String service) {
		if (!this.initStatus) {
			this.service = service;
		}
	}

	/**
	 * 设置 API 接口名称或路径
	 * <p>仅在未初始化状态下生效（{@code initStatus == false}）。</p>
	 *
	 * @param api API 接口名称或路径
	 * @since 1.0.0
	 */
	public void setApi(String api) {
		if (!this.initStatus) {
			this.api = api;
		}
	}

	/**
	 * 设置自定义异常消息
	 * <p>非空时在抛出异常时覆盖远程响应中的错误消息。仅在未初始化状态下生效（{@code initStatus == false}）。</p>
	 *
	 * @param customExceptionMessage 自定义异常消息
	 * @since 1.0.0
	 */
	public void setCustomExceptionMessage(String customExceptionMessage) {
		if (!this.initStatus) {
			this.customExceptionMessage = customExceptionMessage;
		}
	}

	/**
	 * 设置业务码字段名
	 * <p>仅在未初始化状态下生效（{@code initStatus == false}）。</p>
	 *
	 * @param codeField 业务码字段名，不能为空
	 * @throws IllegalArgumentException 当 {@code codeField} 为空时抛出
	 * @since 1.0.0
	 */
	public void setCodeField(String codeField) {
		if (!this.initStatus) {
			Assert.hasText(codeField, "codeField 不可为空");
			this.codeField = codeField;
		}
	}

	/**
	 * 设置错误消息字段名
	 * <p>仅在未初始化状态下生效（{@code initStatus == false}）。</p>
	 *
	 * @param messageField 错误消息字段名，不能为空
	 * @throws IllegalArgumentException 当 {@code messageField} 为空时抛出
	 * @since 1.0.0
	 */
	public void setMessageField(String messageField) {
		if (!this.initStatus) {
			Assert.hasText(messageField, "messageField 不可为空");
			this.messageField = messageField;
		}
	}

	/**
	 * 锁定配置，进入已初始化状态
	 * <p>
	 * 调用后将 {@code initStatus} 置为 {@code true}，后续所有 setter 不再生效。
	 * 首次执行 {@link #hasError(ClientHttpResponse)} 时也会自动置为已初始化状态。
	 * </p>
	 */
	public void init() {
		this.initStatus = true;
	}

	/**
	 * 判断响应是否为错误（结合 HTTP 状态与业务判定）
	 * <p>
	 * 判定规则：
	 * <ul>
	 *   <li>当 HTTP 状态码为错误（4xx/5xx）时，直接视为错误</li>
	 *   <li>当状态码非错误且不为 200（OK）时，视为无错误（跳过业务判定）</li>
	 *   <li>解析响应体为 JSON 对象；不可解析或为空时视为无错误</li>
	 *   <li>若配置了成功判定谓词（{@link #successPredicate}），谓词返回 {@code false} 视为错误，返回 {@code true} 视为无错误</li>
	 *   <li>否则，当存在业务码字段（{@link #codeField}）时：
	 *       业务码不等于成功码（{@link #successCode}）视为错误，相等视为无错误；类型不匹配导致比较异常（{@link ClassCastException}）时视为无错误</li>
	 * </ul>
	 * </p>
	 *
	 * @param response 客户端响应对象
	 * @return 当判定为错误时返回 {@code true}，否则返回 {@code false}
	 * @throws IOException 读取响应体时发生 IO 异常
	 * @since 1.0.0
	 */
	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		if (!this.initStatus) {
			this.initStatus = true;
		}

		if (response.getStatusCode().isError()) {
			return true;
		}
		if (response.getStatusCode().value() != HttpStatus.OK.value()) {
			return false;
		}
		JsonObject responseBody = getResponseBody(response);
		if (Objects.isNull(responseBody)) {
			return false;
		}
		if (Objects.nonNull(this.successPredicate)) {
			return !this.successPredicate.test(responseBody);
		}
		try {
			String errorCode = getErrorCode(responseBody);
			return this.successCode.equals(errorCode);
		} catch (ClassCastException ignored) {
			return false;
		}
	}

	/**
	 * 将错误响应封装为统一异常并抛出
	 * <p>
	 * 行为说明：
	 * <ul>
	 *   <li>当状态码为错误：尝试解析 JSON；为空时仅封装服务、接口、URI 与状态信息</li>
	 *   <li>当状态码非错误（如 200），但业务判定失败：从 JSON 提取业务码（{@link #codeField}）与错误消息（{@link #messageField}）</li>
	 *   <li>构造 {@link HttpRemoteServiceError}（包含服务名、接口名、请求 URI、业务码、错误消息、HTTP 状态码等）</li>
	 *   <li>根据状态码映射异常：{@link HttpStatus#GATEWAY_TIMEOUT} → {@link HttpRemoteServiceTimeoutException}；其他 → {@link HttpRemoteServiceException}</li>
	 *   <li>若设置了自定义异常消息（{@link #customExceptionMessage}），则优先使用该消息</li>
	 * </ul>
	 * </p>
	 *
	 * @param url      请求的完整 URI
	 * @param method   HTTP 请求方法
	 * @param response 客户端响应对象
	 * @throws IOException 读取响应体时发生 IO 异常
	 * @see HttpRemoteServiceError
	 * @see HttpRemoteServiceException
	 * @see HttpRemoteServiceTimeoutException
	 * @since 1.0.0
	 */
	@Override
	public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
		if (response.getStatusCode().isError()) {
			HttpRemoteServiceError remoteServiceError;
			JsonObject responseBody = getResponseBody(response);
			if (Objects.isNull(responseBody)) {
				remoteServiceError = new HttpRemoteServiceError.Builder(this.service, this.api)
					.url(url)
					.httpStatus(response.getStatusCode())
					.build();
			} else {
				String errorCode = getErrorCode(responseBody);
				remoteServiceError = getHttpRemoteServiceError(url, response, errorCode, responseBody);
			}
			throwsException(response.getStatusCode(), remoteServiceError);
		} else {
			JsonObject responseBody = getResponseBody(response);
			String errorCode = getErrorCode(responseBody);
			HttpRemoteServiceError remoteServiceError = getHttpRemoteServiceError(url, response, errorCode, responseBody);
			throwsException(response.getStatusCode(), remoteServiceError);
		}
	}

	/**
	 * 读取并解析响应体为 JSON 对象
	 * <p>
	 * 仅当响应头 {@code Content-Type} 等于 {@code application/json} 或 {@code application/json;charset=UTF-8}
	 * 时尝试解析；否则直接返回 {@code null}。当响应体为空（长度为 0）或解析结果不是 JSON 对象时，返回 {@code null}。
	 * 解析使用 {@link StandardCharsets#UTF_8}。
	 * </p>
	 *
	 * @param response 客户端响应对象
	 * @return 解析得到的 JSON 对象；若不可解析或非 JSON 对象，返回 {@code null}
	 * @throws IOException 读取响应体时发生 IO 异常
	 */
	protected JsonObject getResponseBody(ClientHttpResponse response) throws IOException {
		if (!MediaType.APPLICATION_JSON.equals(response.getHeaders().getContentType()) &&
			!MediaType.APPLICATION_JSON_UTF8.equals(response.getHeaders().getContentType())) {
			return null;
		}

		byte[] bodyBytes = FileCopyUtils.copyToByteArray(response.getBody());
		if (bodyBytes.length == 0) {
			return null;
		}
		String bodyStr = new String(bodyBytes, StandardCharsets.UTF_8);
		JsonElement bodyJson = JsonUtils.parseString(bodyStr);
		if (!bodyJson.isJsonObject()) {
			return null;
		}
		return bodyJson.getAsJsonObject();
	}

	/**
	 * 从响应信息构造 {@link HttpRemoteServiceError}
	 * <p>
	 * 将请求 URI、服务名、接口名、HTTP 状态及从 JSON 中提取到的业务码与错误消息封装为统一的错误模型。
	 * </p>
	 *
	 * @param url          请求的完整 URI
	 * @param response     客户端响应对象
	 * @param errorCode    业务错误码（字符串形式，可为 null）
	 * @param responseBody JSON 响应体对象
	 * @return 封装好的远程服务错误对象
	 * @throws IOException 读取响应体时发生 IO 异常
	 */
	protected HttpRemoteServiceError getHttpRemoteServiceError(URI url, ClientHttpResponse response,
															   @Nullable String errorCode, JsonObject responseBody) throws IOException {
		String errorMessage = null;
		if (responseBody.has(this.messageField)) {
			try {
				JsonPrimitive messageJson = responseBody.getAsJsonPrimitive(this.messageField);
				errorMessage = messageJson.getAsString();
			} catch (ClassCastException ignored) {
			}
		}
		return new HttpRemoteServiceError.Builder(this.service, this.api)
			.url(url)
			.code(errorCode)
			.message(errorMessage)
			.httpStatus(response.getStatusCode())
			.build();
	}

	/**
	 * 从 JSON 响应体中提取业务码并转换为字符串
	 * <p>
	 * 支持数值（转换为整型字符串）、字符串与布尔值（转换为 {@code "true"}/{@code "false"}）。
	 * 其他类型或不存在字段时返回 {@code null}。
	 * </p>
	 *
	 * @param responseBody JSON 响应体对象
	 * @return 字符串形式的业务码；不存在或类型不支持时返回 {@code null}
	 */
	protected String getErrorCode(JsonObject responseBody) {
		if (!responseBody.has(this.codeField)) {
			return null;
		}
		JsonPrimitive codeJson = responseBody.getAsJsonPrimitive(this.codeField);
		if (codeJson.isNumber()) {
			return String.valueOf(codeJson.getAsInt());
		} else if (codeJson.isString()) {
			return codeJson.getAsString();
		} else if (codeJson.isBoolean()) {
			return BooleanUtils.toStringTrueFalse(codeJson.getAsBoolean());
		}
		return null;
	}

	/**
	 * 根据 HTTP 状态抛出统一远程服务异常
	 * <p>
	 * 网关超时（{@link HttpStatus#GATEWAY_TIMEOUT}）抛出 {@link HttpRemoteServiceTimeoutException}；
	 * 其他情况抛出 {@link HttpRemoteServiceException}。当设置了自定义异常消息（{@link #customExceptionMessage}）时优先使用。
	 * </p>
	 *
	 * @param statusCode         HTTP 状态码
	 * @param remoteServiceError 封装的远程服务错误模型
	 */
	protected void throwsException(HttpStatusCode statusCode, HttpRemoteServiceError remoteServiceError) {
		if (statusCode.equals(HttpStatus.GATEWAY_TIMEOUT)) {
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
