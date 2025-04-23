package io.github.pangju666.framework.web.utils;

import io.github.pangju666.commons.io.utils.FileUtils;
import io.github.pangju666.commons.io.utils.IOUtils;
import io.github.pangju666.commons.lang.utils.RegExUtils;
import io.github.pangju666.framework.web.model.common.Range;
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
	public static final Pattern RANGE_PATTERN = Pattern.compile("^bytes=\\d+-\\d*(, ?\\d+-\\d*)*$");
	public static final String RANGES_DELIMITER = ",";
	public static final String RANGE_DELIMITER = "-";
	public static final String REQUEST_RANGE_HEADER_VALUE_PREFIX = "bytes=";
	public static final String RESPONSE_ACCEPT_RANGES_HEADER_VALUE = "bytes";
	public static final String RESPONSE_CONTENT_RANGE_HEADER_PREFIX = "bytes */";
	public static final String RESPONSE_RANGES_CONTENT_DELIMITER = "MULTIPART_BYTERANGES";
	public static final String RESPONSE_RANGES_CONTENT_TYPE = "multipart/byteranges; boundary=" + RESPONSE_RANGES_CONTENT_DELIMITER;
	public static final String RESPONSE_CONTENT_RANGE_FORMAT = "bytes %d-%d/%d";
	public static final String RESPONSE_CONTENT_RANGE_START = "--" + RESPONSE_RANGES_CONTENT_DELIMITER;
	public static final String RESPONSE_CONTENT_RANGE_END = "--" + RESPONSE_RANGES_CONTENT_DELIMITER + "--";
	public static final String NEW_LINE = "\r\n";

	protected FileResponseUtils() {
	}

	public static void handleRangeRequest(final File file, final HttpServletRequest request,
										  final HttpServletResponse response) throws IOException {
		handleRangeRequest(file, null, null, request, response);
	}

	public static void handleRangeRequest(final File file, @Nullable final String downloadFilename, @Nullable final String contentType,
										  final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		Assert.notNull(request, "request 不可为null");
		Assert.notNull(response, "response 不可为null");

		String rangeHeader = request.getHeader(HttpHeaders.RANGE);
		if (StringUtils.isBlank(rangeHeader)) {
			ResponseUtils.setDownloadHeaders(file.length(), downloadFilename, contentType, response);
			ResponseUtils.writeFileToResponse(file, downloadFilename, contentType, response, true);
		} else {
			long totalLength = file.length();
			List<Range> ranges = getRanges(totalLength, rangeHeader);

			// 超出字节数组总长度或格式错误
			if (Objects.isNull(ranges)) {
				response.setHeader(HttpHeaders.CONTENT_RANGE, RESPONSE_CONTENT_RANGE_HEADER_PREFIX + totalLength);
				response.setStatus(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value());
				return;
			}

			// 返回完整内容
			if (ranges.size() == 1 && ranges.get(0).isComplete()) {
				ResponseUtils.setDownloadHeaders(totalLength, downloadFilename, contentType, response);
				ResponseUtils.writeFileToResponse(file, downloadFilename, contentType, response, true);
				return;
			}

			response.setHeader(HttpHeaders.ACCEPT_RANGES, RESPONSE_ACCEPT_RANGES_HEADER_VALUE);
			response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
			writeRangesToResponse(ranges, file, response);
		}
	}

	public static List<Range> getRanges(final long totalLength, @Nullable final String range) {
		if (!RegExUtils.matches(RANGE_PATTERN, range)) {
			return null;
		}

		List<Range> ranges = new ArrayList<>();
		String rangeValue = StringUtils.substringAfter(range, REQUEST_RANGE_HEADER_VALUE_PREFIX);

		for (String part : rangeValue.split(RANGES_DELIMITER)) {
			String[] partRange = part.trim().split(RANGE_DELIMITER);
			long start = Long.parseLong(partRange[0]);

			if (partRange.length == 1) {
				if (start == 0) {
					ranges.add(Range.complete(totalLength));
				} else if (start > totalLength - 1) {
					return null;
				} else {
					ranges.add(new Range(start, totalLength - start - 1, totalLength));
				}
			} else {
				long end = Long.parseLong(partRange[1]);

				if (start == 0 && end == totalLength - 1) {
					return Collections.singletonList(Range.complete(totalLength));
				} else if (start > totalLength - 1 || end > totalLength - 1 || start > end) {
					return null;
				} else {
					ranges.add(new Range(start, end, totalLength));
				}
			}
		}
		return ranges;
	}

	public static void writeRangesToResponse(final List<Range> ranges, final File file,
											 final HttpServletResponse response) throws IOException {
		if (ranges.size() == 1) {
			Range range = ranges.get(0);

			response.setHeader(HttpHeaders.CONTENT_RANGE, RESPONSE_CONTENT_RANGE_FORMAT.formatted(
				range.getStart(), range.getEnd(), range.getTotal()));
			response.setContentLengthLong(range.getLength());
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

			try (OutputStream outputStream = IOUtils.buffer(response.getOutputStream());
				 InputStream inputStream = FileUtils.openUnsynchronizedBufferedInputStream(file)) {
				long skipBytes = 0;
				while (skipBytes != range.getStart()) {
					skipBytes += inputStream.skip(range.getStart() - skipBytes);
				}
				byte[] bytes = inputStream.readNBytes((int) range.getLength());
				outputStream.write(bytes);
			}
		} else {
			try (OutputStream outputStream = IOUtils.buffer(response.getOutputStream());
				 RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
				// 返回文件的多个分段.
				response.setContentType(RESPONSE_RANGES_CONTENT_TYPE);
				response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

				for (Range range : ranges) {
					//为每个Range添加MULTIPART边界和标题字段
					outputStream.write((RESPONSE_CONTENT_RANGE_START + NEW_LINE).getBytes());
					outputStream.write((HttpHeaders.CONTENT_TYPE + ": " + MediaType.APPLICATION_OCTET_STREAM_VALUE
						+ NEW_LINE).getBytes());
					outputStream.write((HttpHeaders.CONTENT_LENGTH + ": " + range.getLength() + NEW_LINE).getBytes());
					outputStream.write((HttpHeaders.CONTENT_RANGE + ": " + RESPONSE_CONTENT_RANGE_FORMAT.formatted(
						range.getStart(), range.getEnd(), range.getTotal()) + NEW_LINE).getBytes());
					outputStream.write(NEW_LINE.getBytes());

					randomAccessFile.seek(range.getStart());
					byte[] buffer = new byte[(int) (range.getEnd() - range.getStart() + 1)];
					randomAccessFile.readFully(buffer);
					outputStream.write(buffer);
					outputStream.write(NEW_LINE.getBytes());
				}
				outputStream.write((RESPONSE_CONTENT_RANGE_END).getBytes());
			}
		}
	}
}
