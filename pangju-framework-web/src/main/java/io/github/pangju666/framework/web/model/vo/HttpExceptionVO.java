package io.github.pangju666.framework.web.model.vo;

import io.github.pangju666.framework.web.enums.HttpExceptionType;

public record HttpExceptionVO(HttpExceptionType type, int code, String description) {
}