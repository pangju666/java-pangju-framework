package io.github.pangju666.framework.web.utils;

import io.github.pangju666.framework.web.annotation.HttpException;
import io.github.pangju666.framework.web.exception.base.BaseHttpException;
import io.github.pangju666.framework.web.model.common.Result;
import io.github.pangju666.framework.web.pool.WebConstants;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ResponseUtils {
	protected static final Logger LOGGER = LoggerFactory.getLogger(ResponseUtils.class);

	protected ResponseUtils() {
	}

	public static void setAttachmentHeader(final HttpServletResponse response, final String filename) {
		setAttachmentHeader(response, filename, StandardCharsets.UTF_8);
	}

	public static void setAttachmentHeader(final HttpServletResponse response, final String filename, final Charset charsets) {
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(filename, charsets));
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
			throw new UncheckedIOException("Http响应写入失败", e);
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
			throw new UncheckedIOException("Http响应写入失败", e);
		}
	}

	public static <T> void writeBeanToResponse(final T bean, final HttpServletResponse response) {
		writeResultToResponse(Result.ok(bean), response, HttpStatus.OK.value());
	}

	public static <T> void writeBeanToResponse(final T bean, final HttpServletResponse response, final HttpStatus status) {
		writeResultToResponse(Result.ok(bean), response, status.value());
	}

	public static <E extends Exception> void writeExceptionToResponse(final E exception, final HttpServletResponse response) {
		if (exception instanceof BaseHttpException httpException) {
			HttpException annotation = exception.getClass().getAnnotation(HttpException.class);
			if (Objects.nonNull(annotation)) {
				Result<Void> result = Result.fail(annotation.type().computeCode(annotation.code()), exception.getMessage());
				if (annotation.log()) {
					httpException.log(LOGGER);
				}
				writeResultToResponse(result, response, annotation.status().value());
			} else {
				Result<Void> result = Result.fail(WebConstants.BASE_ERROR_CODE, exception.getMessage());
				httpException.log(LOGGER);
				writeResultToResponse(result, response, HttpStatus.OK.value());
			}
		} else {
			Result<Void> result = Result.fail(WebConstants.BASE_ERROR_CODE, exception.getMessage());
			LOGGER.error(exception.getMessage(), exception);
			writeResultToResponse(result, response, HttpStatus.OK.value());
		}
	}

	public static <T> void writeResultToResponse(final Result<T> data, final HttpServletResponse response) {
		writeResultToResponse(data, response, HttpStatus.OK.value());
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
			throw new UncheckedIOException("Http响应写入失败", e);
		}
	}
}