package io.github.pangju666.framework.core.lang.pool;

public class RegExPool extends io.github.pangju666.commons.lang.pool.RegExPool {
	/**
	 * 用户名
	 */
	public static final String USERNAME = "[a-zA-Z][a-zA-Z0-9-_]{4,15}";
	/**
	 * 密码强度校验，最少6位，包括至少1个大写字母，1个小写字母，1个数字，1个特殊字符
	 */
	public static final String PASSWORD = "\\S*(?=\\S{6,})(?=\\S*\\d)(?=\\S*[A-Z])(?=\\S*[a-z])(?=\\S*[!@#$%^&*? ])\\S*";

	protected RegExPool() {
	}
}
