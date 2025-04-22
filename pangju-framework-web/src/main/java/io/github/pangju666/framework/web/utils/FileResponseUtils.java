package io.github.pangju666.framework.web.utils;

import io.github.pangju666.commons.io.utils.IOUtils;
import io.github.pangju666.commons.lang.utils.RegExUtils;
import io.github.pangju666.framework.web.model.common.Range;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
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
import java.util.Objects;
import java.util.regex.Pattern;

public class FileResponseUtils {
	/**
	 * 范围请求头模式匹配器
	 * <p>
	 * 用于解析HTTP Range请求头，匹配形如"bytes=0-1024,2048-3072"的范围请求格式。
	 * 支持多个范围段的请求格式解析。
	 * </p>
	 */
	public static final Pattern RANGE_PATTERN = Pattern.compile("^bytes=\\d*-\\d*(,\\d*-\\d*)*$");
	public static final String REQUEST_RANGE_HEADER_VALUE_PREFIX = "bytes=";
	public static final String RESPONSE_ACCEPT_RANGES_HEADER_VALUE = "bytes";
	public static final String RESPONSE_CONTENT_RANGE_HEADER_PREFIX = "bytes */";
	public static final String RANGES_DELIMITER = ",";
	public static final String RANGE_DELIMITER = "-";

	protected FileResponseUtils() {
	}

	public static void handleRangeRequest(final byte[] bytes, @Nullable final String filename, @Nullable final String contentType,
										  final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		Assert.notNull(request, "request 不可为null");
		Assert.notNull(response, "response 不可为null");

		int bytesLength = ArrayUtils.getLength(bytes);
		String rangeHeader = request.getHeader(HttpHeaders.RANGE);

		if (StringUtils.isBlank(rangeHeader)) {
			try (InputStream inputStream = IOUtils.toUnsynchronizedByteArrayInputStream(ArrayUtils.nullToEmpty(bytes));
				 OutputStream outputStream = response.getOutputStream();
				 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
				ResponseUtils.setFileDownloadHeader(bytesLength, filename, contentType, response);
				inputStream.transferTo(bufferedOutputStream);
			}
		} else {
			List<Range> ranges = getRanges(bytesLength, rangeHeader);
			// 超出字节数组总长度或格式错误
			if (Objects.isNull(ranges)) {
				response.setHeader(HttpHeaders.CONTENT_RANGE, RESPONSE_CONTENT_RANGE_HEADER_PREFIX + bytes.length);
				response.sendError(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value());
				return;
			}

			try (InputStream inputStream = IOUtils.toUnsynchronizedByteArrayInputStream(ArrayUtils.nullToEmpty(bytes))) {
				ResponseUtils.setFileDownloadHeader(bytesLength, filename, contentType, response);
				response.setBufferSize(IOUtils.getBufferSize(bytesLength));
				response.setHeader(HttpHeaders.ACCEPT_RANGES, RESPONSE_ACCEPT_RANGES_HEADER_VALUE);
				response.setStatus(HttpStatus.PARTIAL_CONTENT.value());

				writeRangesToResponse(ranges, inputStream, bytesLength, response);
			}
			/*try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
				response.setBufferSize(IOUtils.DEFAULT_BUFFER_SIZE);
				response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");

				List<Range> ranges = getRanges(file, range, response);
				writeRangesToResponse(ranges, randomAccessFile, file.length(), response);
			}*/
		}
	}

	public static List<Range> getRanges(final long totalLength, @Nullable final String range) {
		if (!RegExUtils.matches(RANGE_PATTERN, range)) {
			return null;
		}

		List<Range> ranges = new ArrayList<>();
		String rangeValue = StringUtils.substringAfter(range, REQUEST_RANGE_HEADER_VALUE_PREFIX);

		for (String part : rangeValue.split(RANGES_DELIMITER)) {
			String[] partRange = part.split(RANGE_DELIMITER);
			if (partRange.length == 0) {
				return null;
			} else if (partRange.length == 1) {
				long start = Long.parseLong(partRange[0]);

				if (start == 0) {
					ranges.add(Range.complete(totalLength));
				} else if (start == -1 || start > totalLength - 1) {
					return null;
				} else {
					ranges.add(new Range(start, totalLength - start - 1, totalLength - start));
				}
			} else {
				long start = Long.parseLong(partRange[0]);
				long end = Long.parseLong(partRange[1]);

				if (start == 0 && end == totalLength - 1) {
					return Collections.singletonList(Range.complete(totalLength));
				} else if (start == -1 || end == -1 || start > totalLength - 1 || end > totalLength - 1 || start > end) {
					return null;
				} else {
					ranges.add(new Range(start, end, end - start + 1));
				}
			}
		}
		return ranges;
	}

	public static void writeRangesToResponse(final List<Range> ranges, final InputStream inputStream,
											 final long length, final HttpServletResponse response) throws IOException {
		try (ServletOutputStream servletOutputStream = response.getOutputStream();
			 InputStream bufferedInputStream = IOUtils.unsynchronizedBuffer(inputStream)) {

			if (ranges.size() == 1) {
				Range range = ranges.get(0);

				response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes " + range.getStart() + "-" + range.getEnd() + "/" + range.getTotal());
				response.setContentLengthLong(range.getLength());
				if (!range.isComplete()) {
					response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
				}

				//writeToOutputStream(inputStream, response.getOutputStream(), length, range.getStart(), range.getLength());
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
					//writeToOutputStream(inputStream, response.getOutputStream(), length, range.getStart(), range.getLength());
				}

				servletOutputStream.println();
				servletOutputStream.println("--MULTIPART_BYTERANGES--");
				servletOutputStream.flush();
			}
		}
	}

	protected static void writeToOutputStream(final RandomAccessFile randomAccessFile, final OutputStream output,
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
}
