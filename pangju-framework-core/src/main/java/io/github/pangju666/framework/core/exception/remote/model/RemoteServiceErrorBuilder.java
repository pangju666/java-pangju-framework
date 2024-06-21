package io.github.pangju666.framework.core.exception.remote.model;

import java.net.URI;
import java.text.MessageFormat;

public final class RemoteServiceErrorBuilder {
    private final String service;
    private final String api;
    private URI uri;
    private String message;
    private Integer code;
    private Integer httpStatus;

    public RemoteServiceErrorBuilder(String service, String api) {
        this.service = service;
        this.api = api;
    }

    public RemoteServiceErrorBuilder(String service, String apiPattern, Object... args) {
        this.service = service;
        this.api = MessageFormat.format(apiPattern, args);
    }

    public RemoteServiceErrorBuilder uri(String uri) {
        this.uri = URI.create(uri);
        return this;
    }

    public RemoteServiceErrorBuilder uri(URI uri) {
        this.uri = uri;
        return this;
    }

    public RemoteServiceErrorBuilder uri(String uriPattern, Object... args) {
        this.message = MessageFormat.format(uriPattern, args);
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
        return new RemoteServiceError(service, api, uri, message, code, httpStatus);
    }
}
