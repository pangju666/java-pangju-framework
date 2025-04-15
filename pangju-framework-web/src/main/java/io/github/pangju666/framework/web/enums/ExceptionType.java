package io.github.pangju666.framework.web.enums;

public enum ExceptionType {
	SERVER(5, "服务器内部错误"),
	SERVICE(1, "业务逻辑错误"),
	DATA(2, "数据错误"),
	AUTHENTICATION(3, "业务逻辑错误"),
	VALIDATION(4, "参数校验错误"),
	CUSTOM(0, "自定义错误");

	private final int prefix;
	private final String description;

	ExceptionType(Integer prefix, String description) {
		this.prefix = prefix;
		this.description = description;
	}

	public int getPrefix() {
		return prefix;
	}

	public String getDescription() {
		return description;
	}
}
