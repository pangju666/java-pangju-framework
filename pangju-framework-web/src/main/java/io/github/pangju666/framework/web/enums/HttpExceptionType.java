package io.github.pangju666.framework.web.enums;

public enum HttpExceptionType {
	SERVER("服务器内部错误"),
	SERVICE("业务逻辑错误"),
	DATA("数据错误"),
	AUTHENTICATION("业务逻辑错误"),
	VALIDATION("参数校验错误"),
	CUSTOM("自定义错误");

	private final String description;

	HttpExceptionType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
