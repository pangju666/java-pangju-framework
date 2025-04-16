package io.github.pangju666.framework.web.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.pangju666.commons.lang.utils.JsonUtils;
import io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceException;
import io.github.pangju666.framework.web.pool.WebConstants;
import io.github.pangju666.framework.web.utils.RemoteServiceErrorBuilder;
import org.slf4j.Logger;

import java.net.URI;
import java.util.Optional;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public final class Result<T> {
	public static final String DEFAULT_SUCCESS_MESSAGE = "请求成功";
	public static final String DEFAULT_FAILURE_MESSAGE = "请求失败";

	private final int code;
	private final String message;
	private final T data;

	private Result(int code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public static Result<Void> ok() {
		return new Result<>(WebConstants.SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, null);
	}

	public static <T> Result<T> ok(T data) {
		return new Result<>(WebConstants.SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, data);
	}

	public static Result<Void> fail() {
		return new Result<>(WebConstants.BASE_ERROR_CODE, DEFAULT_FAILURE_MESSAGE, null);
	}

	public static Result<Void> fail(String message) {
		return new Result<>(WebConstants.BASE_ERROR_CODE, message, null);
	}

	public static Result<Void> fail(int code, String message) {
		return new Result<>(code == WebConstants.SUCCESS_CODE ? WebConstants.BASE_ERROR_CODE : code, message, null);
	}

	public T getDataIfSuccess(final String service, final String api, final URI uri) {
		if (this.code == WebConstants.SUCCESS_CODE) {
			return this.data;
		}

		HttpRemoteServiceError httpRemoteServiceError = new RemoteServiceErrorBuilder(service, api, uri)
			.code(this.code)
			.message(this.message)
			.build();
		throw new HttpRemoteServiceException(httpRemoteServiceError);
	}

	public Optional<T> geOptionalData(final String service, final String api, final URI uri, final Logger logger) {
		if (this.code == WebConstants.SUCCESS_CODE) {
			return Optional.ofNullable(this.data);
		}

		HttpRemoteServiceError httpRemoteServiceError = new RemoteServiceErrorBuilder(service, api, uri)
			.code(this.code)
			.message(this.message)
			.build();
		HttpRemoteServiceException httpRemoteServiceException = new HttpRemoteServiceException(httpRemoteServiceError);
		httpRemoteServiceException.log(logger);
		return Optional.empty();
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public T getData() {
		return data;
	}

	@Override
	public String toString() {
		return JsonUtils.toString(this);
	}
}