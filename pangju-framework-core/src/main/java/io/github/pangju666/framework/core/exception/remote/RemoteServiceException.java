package io.github.pangju666.framework.core.exception.remote;

import io.github.pangju666.framework.core.exception.base.ServiceException;
import io.github.pangju666.framework.core.exception.remote.model.RemoteServiceError;
import io.github.pangju666.framework.core.lang.pool.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.util.Objects;

public class RemoteServiceException extends ServiceException {
	protected static final String DEFAULT_MESSAGE = "远程服务请求失败";

	private final RemoteServiceError remoteServiceError;

	public RemoteServiceException(RemoteServiceError remoteServiceError) {
		super(Constants.REMOTE_SERVICE_ERROR_RESPONSE_CODE, DEFAULT_MESSAGE);
		this.remoteServiceError = remoteServiceError;
	}

	public RemoteServiceException(RemoteServiceError remoteServiceError, String message) {
		super(Constants.REMOTE_SERVICE_ERROR_RESPONSE_CODE, message);
		this.remoteServiceError = remoteServiceError;
	}

	public RemoteServiceException(RemoteServiceError remoteServiceError, Throwable cause) {
		super(Constants.REMOTE_SERVICE_ERROR_RESPONSE_CODE, DEFAULT_MESSAGE, cause);
		this.remoteServiceError = remoteServiceError;
	}

	public RemoteServiceException(RemoteServiceError remoteServiceError, String message, Throwable cause) {
		super(Constants.REMOTE_SERVICE_ERROR_RESPONSE_CODE, message, cause);
		this.remoteServiceError = remoteServiceError;
	}

	protected RemoteServiceException(RemoteServiceError remoteServiceError, int code, String message) {
		super(code, message);
		this.remoteServiceError = remoteServiceError;
	}

	protected RemoteServiceException(RemoteServiceError remoteServiceError, int code, String message, Throwable cause) {
		super(code, message, cause);
		this.remoteServiceError = remoteServiceError;
	}

	public RemoteServiceError getRemoteService() {
		return remoteServiceError;
	}

	@Override
	public void log(Logger logger) {
		log(logger, Level.ERROR);
	}

	@Override
	public void log(Logger logger, Level level) {
		StringBuilder builder = new StringBuilder()
			.append("服务：")
			.append(this.remoteServiceError.service())
			.append(" 接口：")
			.append(this.remoteServiceError.api());
		if (Objects.nonNull(this.remoteServiceError.uri())) {
			builder.append(" url：").append(this.remoteServiceError.uri());
		}
		builder.append(" 请求失败");
		if (Objects.nonNull(this.getRemoteService().httpStatus())) {
			builder.append(" http状态码：").append(this.getRemoteService().httpStatus()).append("，");
		}
		if (Objects.nonNull(this.getRemoteService().code())) {
			builder.append(" 错误码：").append(this.getRemoteService().code()).append("，");
		}
		if (StringUtils.isNotBlank(this.getRemoteService().message())) {
			builder.append(" 错误信息：").append(this.getRemoteService().message());
		}
		logger.atLevel(level).setCause(this).log(builder.toString());
	}
}