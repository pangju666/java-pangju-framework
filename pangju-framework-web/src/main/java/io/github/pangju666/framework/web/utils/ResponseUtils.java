/*
 *   Copyright 2025 pangju666
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.pangju666.framework.web.utils;

import io.github.pangju666.framework.web.annotation.HttpException;
import io.github.pangju666.framework.web.exception.base.BaseHttpException;
import io.github.pangju666.framework.web.model.common.Range;
import io.github.pangju666.framework.web.model.common.Result;
import io.github.pangju666.framework.web.pool.WebConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ResponseUtils {
	protected static final Logger LOGGER = LoggerFactory.getLogger(ResponseUtils.class);
	protected static final Pattern RANGE_PATTERN = Pattern.compile("^bytes=\\d*-\\d*(,\\d*-\\d*)*$");

	protected ResponseUtils() {
	}

	public static void setAttachmentHeader(final HttpServletResponse response, final String filename) {
		setAttachmentHeader(response, filename, StandardCharsets.UTF_8);
	}

	public static void setAttachmentHeader(final HttpServletResponse response, final String filename, final Charset charsets) {
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(filename, charsets));
	}

	public static void writeFileToResponse(final File file, final String expectFileName, final String contentType,
										   final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		String fileName = computeFileName(file, expectFileName);
		ResponseUtils.setAttachmentHeader(response, fileName);
		response.setContentType(contentType);
		response.setContentLengthLong(file.length());

		String range = request.getHeader(HttpHeaders.RANGE);
		if (StringUtils.isBlank(range)) {
			try (InputStream fileInputStream = new FileInputStream(file);
				 InputStream inputStream = new BufferedInputStream(fileInputStream);
				 OutputStream outputStream = new BufferedOutputStream(response.getOutputStream())) {
				inputStream.transferTo(outputStream);
				outputStream.flush();
			}
		} else {
			try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
				response.setBufferSize(IOUtils.DEFAULT_BUFFER_SIZE);
				response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");

				List<Range> ranges = getRanges(file, range, response);
				writeRangesToResponse(ranges, randomAccessFile, file.length(), response);
			}
		}
		response.flushBuffer();
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
					httpException.log(LOGGER, Level.ERROR);
				}
				writeResultToResponse(result, response, annotation.status().value());
			} else {
				Result<Void> result = Result.fail(WebConstants.BASE_ERROR_CODE, exception.getMessage());
				httpException.log(LOGGER, Level.ERROR);
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

	protected static String computeFileName(File file, String expectFileName) {
		String extension = FilenameUtils.getExtension(file.getName());
		String fileName = file.getName();
		if (StringUtils.isNotBlank(expectFileName)) {
			fileName = expectFileName + FilenameUtils.EXTENSION_SEPARATOR + extension;
		}
		return URLEncoder.encode(fileName, StandardCharsets.UTF_8);
	}

	protected static List<Range> getRanges(File file, String rangeValue, HttpServletResponse response) throws IOException {
		long fileLength = file.length();
		List<Range> ranges = new ArrayList<>();

		if (!RANGE_PATTERN.matcher(rangeValue).matches()) {
			response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength);
			response.sendError(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value());
			return Collections.emptyList();
		}

		rangeValue = StringUtils.substringAfter(rangeValue, "bytes=");
		for (String part : rangeValue.split(",")) {
			part = part.split("/")[0];

			int delimiterIndex = part.indexOf("-");
			long start = rangePartToLong(part, 0, delimiterIndex);
			long end = rangePartToLong(part, delimiterIndex + 1, part.length());

			if (start == 0 && end == fileLength - 1) {
				Range fullRange = new Range(0, fileLength - 1, fileLength);
				fullRange.setFull(true);
				return Collections.singletonList(fullRange);
			}

			if (start == -1) {
				start = fileLength - end;
				end = fileLength - 1;
			} else if (end == -1 || end > fileLength - 1) {
				end = fileLength - 1;
			}

			if (start > end) {
				response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength);
				response.sendError(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value());
				return Collections.emptyList();
			}

			ranges.add(new Range(start, end, end - start + 1));
		}
		return ranges;
	}

	protected static void writeRangesToResponse(List<Range> ranges, RandomAccessFile randomAccessFile,
												long length, HttpServletResponse response) throws IOException {
		try (ServletOutputStream servletOutputStream = response.getOutputStream()) {
			if (ranges.size() <= 1) {
				Range range = ranges.get(0);

				response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes " + range.getStart() + "-" + range.getEnd() + "/" + range.getTotal());
				response.setContentLengthLong(range.getLength());
				if (!range.isFull()) {
					response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
				}

				writeFileToOutputStream(randomAccessFile, response.getOutputStream(), length, range.getStart(), range.getLength());
				servletOutputStream.flush();
			} else {
				// 返回文件的多个分段.
				response.setContentType("multipart/byteranges; boundary=MULTIPART_BYTERANGES");
				response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

				// 复制多个文件分段.
				for (Range range : ranges) {
					//为每个Range添加MULTIPART边界和标题字段
					servletOutputStream.println();
					servletOutputStream.println("--MULTIPART_BYTERANGES");
					servletOutputStream.println(HttpHeaders.CONTENT_TYPE + ": " + MediaType.APPLICATION_OCTET_STREAM_VALUE);
					servletOutputStream.println(HttpHeaders.CONTENT_LENGTH + ": " + range.getLength());
					servletOutputStream.println(HttpHeaders.CONTENT_RANGE + ": bytes " + range.getStart() + "-" + range.getEnd() + "/" + range.getTotal());

					// 复制多个需要复制的文件分段当中的一个分段.
					writeFileToOutputStream(randomAccessFile, response.getOutputStream(), length, range.getStart(), range.getLength());
				}

				servletOutputStream.println();
				servletOutputStream.println("--MULTIPART_BYTERANGES--");
				servletOutputStream.flush();
			}
		}
	}

	protected static void writeFileToOutputStream(RandomAccessFile randomAccessFile, OutputStream output,
												  long fileSize, long start, long length) throws IOException {
		byte[] buffer = new byte[4096];
		int read = 0;
		long transmitted = 0;
		if (fileSize == length) {
			randomAccessFile.seek(start);
			//需要下载的文件长度与文件长度相同，下载整个文件.
			while ((transmitted + read) <= length && (read = randomAccessFile.read(buffer)) != -1) {
				output.write(buffer, 0, read);
				transmitted += read;
			}
			//处理最后不足buff大小的部分
			if (transmitted < length) {
				read = randomAccessFile.read(buffer, 0, (int) (length - transmitted));
				output.write(buffer, 0, read);
			}
		} else {
			randomAccessFile.seek(start);
			long toRead = length;

			//如果需要读取的片段，比单次读取的4096小，则使用读取片段大小读取
			if (toRead < buffer.length) {
				output.write(buffer, 0, randomAccessFile.read(new byte[(int) toRead]));
				return;
			}
			while ((read = randomAccessFile.read(buffer)) > 0) {
				toRead -= read;
				if (toRead > 0) {
					output.write(buffer, 0, read);
				} else {
					output.write(buffer, 0, (int) toRead + read);
					break;
				}
			}
		}
	}

	protected static Long rangePartToLong(String part, int beginIndex, int endIndex) {
		String substring = part.substring(beginIndex, endIndex);
		return (!substring.isEmpty()) ? Long.parseLong(substring) : -1;
	}
}