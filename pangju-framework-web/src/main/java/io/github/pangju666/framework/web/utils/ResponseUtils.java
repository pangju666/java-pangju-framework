package io.github.pangju666.framework.web.utils;

import io.github.pangju666.commons.lang.utils.JsonUtils;
import io.github.pangju666.framework.core.exception.base.BaseRuntimeException;
import io.github.pangju666.framework.core.exception.base.ServerException;
import io.github.pangju666.framework.web.model.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ResponseUtils {
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
		writeResultToResponse(Result.failByException(exception), response, exception.getHttpStatus());
	}

	public static <E extends BaseRuntimeException> void writeExceptionToResponse(final E exception, final HttpServletResponse response, final HttpStatus httpStatus) {
		writeResultToResponse(Result.failByException(exception), response, httpStatus.value());
	}

	public static <E extends BaseRuntimeException> void writeExceptionToResponse(final E exception, final HttpServletResponse response, int httpStatus) {
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
			outputStream.write(JsonUtils.toString(data).getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new ServerException("Http Servlet 响应值写入失败", e);
		}
	}
}