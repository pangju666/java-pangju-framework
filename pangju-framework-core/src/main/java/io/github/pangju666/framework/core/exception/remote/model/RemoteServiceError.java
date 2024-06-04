package io.github.pangju666.framework.core.exception.remote.model;

import java.net.URI;

public record RemoteServiceError(String service,
								 String api,
								 URI uri,
								 String message,
								 Integer code,
								 Integer httpStatus) {
}
