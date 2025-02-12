package io.github.pangju666.framework.core.exception.remote.model;

import java.net.URI;

public record RemoteServiceError(String service,
								 String api,
								 URI uri,
								 String message,
								 Integer code,
								 Integer httpStatus) {

	public RemoteServiceError clone(String message) {
		return new RemoteServiceError(service, api, uri, message, code, httpStatus);
	}
}