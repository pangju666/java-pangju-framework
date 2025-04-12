package io.github.pangju666.framework.web.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.pangju666.commons.lang.utils.JsonUtils;
import io.github.pangju666.framework.web.exception.base.BaseRuntimeException;
import io.github.pangju666.framework.web.exception.remote.RemoteServiceError;
import io.github.pangju666.framework.web.exception.remote.RemoteServiceErrorBuilder;
import io.github.pangju666.framework.web.exception.remote.RemoteServiceException;
import io.github.pangju666.framework.web.lang.pool.WebConstants;
import org.slf4j.Logger;

import java.net.URI;
import java.util.Optional;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public record Result<T>(
	String message,
	Integer code,
	T data) {
	public static Result<Void> ok() {
		return new Result<>(WebConstants.RESULT_SUCCESS_MESSAGE, WebConstants.SUCCESS_CODE, null);
	}

	public static Result<Void> okByMessage(String message) {
		return new Result<>(message, WebConstants.SUCCESS_CODE, null);
	}

	public static <T> Result<T> ok(T data) {
		return new Result<>(WebConstants.RESULT_SUCCESS_MESSAGE, WebConstants.SUCCESS_CODE, data);
	}

	public static <E extends BaseRuntimeException> Result<Void> failByException(E e) {
		return new Result<>(e.getMessage(), e.getCode(), null);
	}

	public static Result<Void> fail() {
		return new Result<>(WebConstants.RESULT_FAILURE_MESSAGE, WebConstants.BASE_ERROR_CODE, null);
	}

	public static Result<Void> failByMessage(String message) {
		return new Result<>(message, WebConstants.BASE_ERROR_CODE, null);
	}

	public static <T> Result<T> fail(T data) {
		return new Result<>(WebConstants.RESULT_FAILURE_MESSAGE, WebConstants.BASE_ERROR_CODE, data);
	}

	public Optional<T> getOptionalData(final String service, final String api, final URI uri) {
		if (this.code == WebConstants.SUCCESS_CODE) {
			return Optional.ofNullable(this.data);
		}

		RemoteServiceError remoteServiceError = new RemoteServiceErrorBuilder(service, api, uri)
			.code(this.code)
			.message(this.message)
			.build();
		throw new RemoteServiceException(remoteServiceError);
	}

	public Optional<T> geOptionalData(final String service, final String api, final URI uri, final Logger logger) {
		if (this.code == WebConstants.SUCCESS_CODE) {
			return Optional.ofNullable(this.data);
		}

		RemoteServiceError remoteServiceError = new RemoteServiceErrorBuilder(service, api, uri)
			.code(this.code)
			.message(this.message)
			.build();
		RemoteServiceException remoteServiceException = new RemoteServiceException(remoteServiceError);
		remoteServiceException.log(logger);
		return Optional.empty();
	}

	@Override
	public String toString() {
		return JsonUtils.toString(this);
	}
}