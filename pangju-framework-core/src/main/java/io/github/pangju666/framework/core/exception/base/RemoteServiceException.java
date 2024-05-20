package io.github.pangju666.framework.core.exception.base;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.event.Level;

public class RemoteServiceException extends BaseRuntimeException {
	private final String service;
	private final String apiPath;
	;
	private String responseMessage;
	private int responseCode;

	public RemoteServiceException(String service, String apiPath, String message) {
		super(ConstantPool.REMOTE_SERVICE_ERROR_RESPONSE_CODE, message);
		this.service = service;
		this.apiPath = apiPath;
	}

	public RemoteServiceException(String service, String apiPath, String message, Throwable cause) {
		super(ConstantPool.REMOTE_SERVICE_ERROR_RESPONSE_CODE, message, cause);
		this.service = service;
		this.apiPath = apiPath;
	}

	public RemoteServiceException(String service, String apiPath, String responseMessage, int responseCode) {
		super(ConstantPool.REMOTE_SERVICE_ERROR_RESPONSE_CODE, responseMessage);
		this.service = service;
		this.apiPath = apiPath;
		this.responseMessage = responseMessage;
		this.responseCode = responseCode;
	}

	public RemoteServiceException(String service, String apiPath, String responseMessage, int responseCode, String message) {
		super(ConstantPool.REMOTE_SERVICE_ERROR_RESPONSE_CODE, message);
		this.service = service;
		this.apiPath = apiPath;
		this.responseMessage = responseMessage;
		this.responseCode = responseCode;
	}

	public RemoteServiceException(String service, String apiPath, String responseMessage, int responseCode, String message, Throwable cause) {
		super(ConstantPool.REMOTE_SERVICE_ERROR_RESPONSE_CODE, message, cause);
		this.service = service;
		this.apiPath = apiPath;
		this.responseMessage = responseMessage;
		this.responseCode = responseCode;
	}

	public String getService() {
		return service;
	}

	public String getApiPath() {
		return apiPath;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	@Override
	public void log(Logger logger) {
		logger.error("服务：{}，接口路径：{} 请求失败，错误码：{}，错误信息：{}",
			StringUtils.defaultString(service),
			StringUtils.defaultString(apiPath),
			responseCode == 0 ? "无" : responseCode,
			StringUtils.defaultIfBlank(responseMessage, "无"));
	}

	@Override
	public void log(Logger logger, Level level) {
		logger.atLevel(level)
			.log("服务：{}，接口路径：{} 请求失败，错误码：{}，错误信息：{}",
				StringUtils.defaultString(service),
				StringUtils.defaultString(apiPath),
				responseCode == 0 ? "无" : responseCode,
				StringUtils.defaultIfBlank(responseMessage, "无")
			);
	}
}
