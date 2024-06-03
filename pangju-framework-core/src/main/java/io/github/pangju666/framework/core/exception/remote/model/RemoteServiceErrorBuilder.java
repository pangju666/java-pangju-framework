package io.github.pangju666.framework.core.exception.remote.model;

import java.text.MessageFormat;

public final class RemoteServiceErrorBuilder {
	private final String service;
	private final String api;
	private String path;
	private String message;
	private Integer code;
	private Integer httpStatus;

	private RemoteServiceErrorBuilder(String service, String api) {
		this.service = service;
		this.api = api;
	}

	private RemoteServiceErrorBuilder(String service, String apiPattern, Object... args) {
		this.service = service;
		this.api = MessageFormat.format(apiPattern, args);
	}

	public static RemoteServiceErrorBuilder newInstance(String service, String api) {
		return new RemoteServiceErrorBuilder(service, api);
	}

	public static RemoteServiceErrorBuilder newInstance(String service, String apiPattern, Object... args) {
		return new RemoteServiceErrorBuilder(service, apiPattern, args);
	}

	public RemoteServiceErrorBuilder path(String path) {
		this.path = path;
		return this;
	}

	public RemoteServiceErrorBuilder path(String pathPattern, Object... args) {
		this.message = MessageFormat.format(pathPattern, args);
		return this;
	}

	public RemoteServiceErrorBuilder message(String message) {
		this.message = message;
		return this;
	}

	public RemoteServiceErrorBuilder message(String messagePattern, Object... args) {
		this.message = MessageFormat.format(messagePattern, args);
		return this;
	}

	public RemoteServiceErrorBuilder code(Integer code) {
		this.code = code;
		return this;
	}

	public RemoteServiceErrorBuilder httpStatus(Integer httpStatus) {
		this.httpStatus = httpStatus;
		return this;
	}

	public RemoteServiceError build() {
		return new RemoteServiceError(service, api, path, message, code, httpStatus);
	}
}
