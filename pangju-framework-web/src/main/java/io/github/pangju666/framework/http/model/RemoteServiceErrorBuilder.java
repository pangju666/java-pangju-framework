package io.github.pangju666.framework.http.model;

import org.springframework.http.HttpStatus;

import java.net.URI;
import java.text.MessageFormat;

public final class RemoteServiceErrorBuilder {
	private final String service;
	private final String api;
	private URI uri;
	private String message;
	private Integer code;
	private int httpStatus = HttpStatus.OK.value();

	public RemoteServiceErrorBuilder(String service, String api) {
		this.service = service;
		this.api = api;
	}

	public RemoteServiceErrorBuilder(String service, String api, URI uri) {
		this.service = service;
		this.api = api;
		this.uri = uri;
	}

	public RemoteServiceErrorBuilder uri(URI uri) {
		this.uri = uri;
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

	public RemoteServiceErrorBuilder httpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
		return this;
	}

	public RemoteServiceError build() {
		return new RemoteServiceError(service, api, uri, message, code, httpStatus);
	}
}