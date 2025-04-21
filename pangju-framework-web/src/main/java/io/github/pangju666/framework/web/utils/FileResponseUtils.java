package io.github.pangju666.framework.web.utils;

import io.github.pangju666.commons.io.utils.FileUtils;
import io.github.pangju666.commons.io.utils.FilenameUtils;
import io.github.pangju666.commons.io.utils.IOUtils;
import io.github.pangju666.framework.web.model.common.Range;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class FileResponseUtils {
	/**
	 * 范围请求头模式匹配器
	 * <p>
	 * 用于解析HTTP Range请求头，匹配形如"bytes=0-1024,2048-3072"的范围请求格式。
	 * 支持多个范围段的请求格式解析。
	 * </p>
	 */
	protected static final Pattern RANGE_PATTERN = Pattern.compile("^bytes=\\d*-\\d*(,\\d*-\\d*)*$");

	protected FileResponseUtils() {
	}

	public static void writeFileToResponse(final File file, @Nullable final String responseFilename, @Nullable final String contentType,
										   final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		Assert.notNull(request, "request 不可为null");
		Assert.notNull(response, "response 不可为null");
		FileUtils.checkFile(file, "file 不可为null");

		if (StringUtils.isNotBlank(responseFilename)) {
			String attachmentFilename = FilenameUtils.replaceBaseName(file.getName(), responseFilename);
			ResponseUtils.setAttachmentHeader(response, attachmentFilename);
		}
		response.setContentType(contentType);
		response.setContentLengthLong(file.length());

		String range = request.getHeader(HttpHeaders.RANGE);
		if (StringUtils.isBlank(range)) {
			try (InputStream inputStream = FileUtils.openUnsynchronizedBufferedInputStream(file);
				 OutputStream outputStream = response.getOutputStream();
				 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
				inputStream.transferTo(bufferedOutputStream);
			}
		} else {
			try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
				response.setBufferSize(IOUtils.DEFAULT_BUFFER_SIZE);
				response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");

				List<Range> ranges = getRanges(file, range, response);
				writeRangesToResponse(ranges, randomAccessFile, file.length(), response);
			}
		}
	}

	protected static List<Range> getRanges(final File file, String rangeValue, final HttpServletResponse response) throws IOException {
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

	protected static void writeRangesToResponse(final List<Range> ranges, final RandomAccessFile randomAccessFile,
												final long length, final HttpServletResponse response) throws IOException {
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

	protected static void writeFileToOutputStream(final RandomAccessFile randomAccessFile, final OutputStream output,
												  final long fileSize, final long start, final long length) throws IOException {
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

	protected static Long rangePartToLong(final String part, final int beginIndex, final int endIndex) {
		String substring = part.substring(beginIndex, endIndex);
		return (!substring.isEmpty()) ? Long.parseLong(substring) : -1;
	}
}
