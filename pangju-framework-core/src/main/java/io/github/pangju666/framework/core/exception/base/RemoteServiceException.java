package io.github.pangju666.framework.core.exception.base;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.util.Objects;

public class RemoteServiceException extends BaseRuntimeException {
	private final String service;
	private final String api;
	private String path;
	private String responseMessage;
	private Integer responseCode;

	public RemoteServiceException(String service, String api, String message) {
		super(ConstantPool.REMOTE_SERVICE_ERROR_RESPONSE_CODE, message);
		this.service = service;
		this.api = api;
	}

	public RemoteServiceException(String service, String api, int code, String message) {
		super(code, message);
		this.service = service;
		this.api = api;
	}

	public RemoteServiceException(String service, String api, String message, Throwable cause) {
		super(ConstantPool.REMOTE_SERVICE_ERROR_RESPONSE_CODE, message, cause);
		this.service = service;
		this.api = api;
	}

	public RemoteServiceException(String service, String api, int code, String message, Throwable cause) {
		super(code, message, cause);
		this.service = service;
		this.api = api;
	}

	public String getService() {
		return service;
	}

	public String getApi() {
		return api;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}

	@Override
	public void log(Logger logger) {
		log(logger, Level.ERROR);
	}

	@Override
	public void log(Logger logger, Level level) {
		StringBuilder builder = new StringBuilder()
			.append("服务：")
			.append(this.service)
			.append(" 接口：")
			.append(this.api);
		if (StringUtils.isNotBlank(path)) {
			builder.append(" 路径：").append(this.path);
		}
		builder.append(" 请求失败");
		if (Objects.nonNull(responseCode)) {
			builder.append(" 错误码：").append(this.responseCode).append("，");
		}
		if (StringUtils.isNotBlank(responseMessage)) {
			builder.append(" 错误信息：").append(this.responseMessage).append("，");
		}
		logger.atLevel(level).log(builder.toString());
	}
}
