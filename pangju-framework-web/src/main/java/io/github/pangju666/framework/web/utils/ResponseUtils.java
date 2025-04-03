package io.github.pangju666.framework.web.utils;

import io.github.pangju666.framework.web.annotation.IgnoreLog;
import io.github.pangju666.framework.web.exception.base.BaseRuntimeException;
import io.github.pangju666.framework.web.exception.base.ServerException;
import io.github.pangju666.framework.web.model.vo.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ResponseUtils {
	protected static final Logger logger = LoggerFactory.getLogger(ResponseUtils.class);

	protected ResponseUtils() {
	}

	public static void setAttachmentHeader(final HttpServletResponse response, final String filename) {
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
	}

	public static void writeBytesToResponse(final byte[] bytes, final HttpServletResponse response) {
		writeBytesToResponse(bytes, response, HttpStatus.OK.value());
	}

	public static void writeBytesToResponse(final byte[] bytes, final HttpServletResponse response, final HttpStatus status) {
		writeBytesToResponse(bytes, response, status.value());
	}

	public static void writeBytesToResponse(final byte[] bytes, final HttpServletResponse response, int status) {
		try (OutputStream outputStream = response.getOutputStream()) {
			response.setStatus(status);
			outputStream.write(bytes);
		} catch (IOException e) {
			throw new ServerException("Http Servlet 响应值写入失败", e);
		}
	}

	public static void writeInputStreamToResponse(final InputStream inputStream, final HttpServletResponse response) {
		writeInputStreamToResponse(inputStream, response, HttpStatus.OK.value());
	}

	public static void writeInputStreamToResponse(final InputStream inputStream, final HttpServletResponse response, final HttpStatus status) {
		writeInputStreamToResponse(inputStream, response, status.value());
	}

	public static void writeInputStreamToResponse(final InputStream inputStream, final HttpServletResponse response, int status) {
		try (OutputStream outputStream = response.getOutputStream()) {
			response.setStatus(status);
			inputStream.transferTo(outputStream);
		} catch (IOException e) {
			throw new ServerException("Http Servlet 响应值写入失败", e);
		}
	}

	public static <T> void writeBeanToResponse(final T bean, final HttpServletResponse response) {
		writeResultToResponse(Result.ok(bean), response, HttpStatus.OK.value());
	}

	public static <T> void writeBeanToResponse(final T bean, final HttpServletResponse response, final HttpStatus status) {
		writeResultToResponse(Result.ok(bean), response, status.value());
	}

	public static <E extends BaseRuntimeException> void writeExceptionToResponse(final E exception, final HttpServletResponse response) {
		if (Objects.isNull(exception.getClass().getAnnotation(IgnoreLog.class))) {
			exception.log(logger);
		}
		ResponseStatus responseStatus = exception.getClass().getAnnotation(ResponseStatus.class);
		if (Objects.nonNull(responseStatus)) {
			writeResultToResponse(Result.failByException(exception), response, responseStatus.value().value());
		} else {
			writeResultToResponse(Result.failByException(exception), response, HttpStatus.OK.value());
		}
	}

	public static <E extends BaseRuntimeException> void writeExceptionToResponse(final E exception, final HttpServletResponse response, final HttpStatus httpStatus) {
		if (Objects.isNull(exception.getClass().getAnnotation(IgnoreLog.class))) {
			exception.log(logger);
		}
		writeResultToResponse(Result.failByException(exception), response, httpStatus.value());
	}

	public static <E extends BaseRuntimeException> void writeExceptionToResponse(final E exception, final HttpServletResponse response, int httpStatus) {
		if (Objects.isNull(exception.getClass().getAnnotation(IgnoreLog.class))) {
			exception.log(logger);
		}
		writeResultToResponse(Result.failByException(exception), response, httpStatus);
	}

	public static <T> void writeResultToResponse(final Result<T> data, final HttpServletResponse response, final HttpStatus httpStatus) {
		writeResultToResponse(data, response, httpStatus.value());
	}

	public static <T> void writeResultToResponse(final Result<T> data, final HttpServletResponse response, int status) {
		try (OutputStream outputStream = response.getOutputStream()) {
			response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
			response.setStatus(status);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			outputStream.write(data.toString().getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new ServerException("Http Servlet 响应值写入失败", e);
		}
	}
}