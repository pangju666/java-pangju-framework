package com.test.exception

import io.github.pangju666.framework.web.annotation.HttpException
import io.github.pangju666.framework.web.enums.HttpExceptionType
import io.github.pangju666.framework.web.exception.base.ServiceException

@HttpException(code = 1200, type = HttpExceptionType.SERVICE, description = "测试异常")
class TestException extends ServiceException {
	TestException(String message) {
		super(message)
	}

	TestException(String message, String reason) {
		super(message, reason)
	}

	TestException(String message, Throwable cause) {
		super(message, cause)
	}

	TestException(String message, String reason, Throwable cause) {
		super(message, reason, cause)
	}
}
