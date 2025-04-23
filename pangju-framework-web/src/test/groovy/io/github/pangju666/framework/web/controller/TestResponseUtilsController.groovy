/*
 *    Copyright 2025 pangju666
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *         http://www.apache.org/licenses/LICENSE-2.0
 *     
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package io.github.pangju666.framework.web.controller

import io.github.pangju666.commons.io.utils.IOUtils
import io.github.pangju666.framework.web.model.common.Result
import io.github.pangju666.framework.web.utils.ResponseUtils
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.lang3.RandomUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import java.nio.charset.StandardCharsets

@RequestMapping("/response-utils")
@RestController
class TestResponseUtilsController {
	def bytes = RandomUtils.nextBytes(1000000)
	def bytes2 = RandomUtils.nextBytes(100000000)

	@GetMapping("/bytes-buffer")
	void bufferBytes(HttpServletResponse response) {
		ResponseUtils.writeBytesToResponse(bytes, response)
	}

	@GetMapping("/bytes")
	void Bytes(HttpServletResponse response) {
		try (OutputStream outputStream = response.getOutputStream()) {
			InputStream inputStream = IOUtils.toUnsynchronizedByteArrayInputStream(bytes)
			inputStream.transferTo(outputStream)
		} catch (IOException e) {
			throw new UncheckedIOException(e)
		}
	}

	@GetMapping("/inputStream-buffer")
	void bufferInputStream(HttpServletResponse response) {
		try (InputStream inputStream = IOUtils.toUnsynchronizedByteArrayInputStream(bytes2)) {
			ResponseUtils.writeInputStreamToResponse(inputStream, response)
		}
	}

	@GetMapping("/inputStream")
	void inputStream(HttpServletResponse response) {
		try (OutputStream outputStream = response.getOutputStream();
			 InputStream inputStream = IOUtils.toUnsynchronizedByteArrayInputStream(bytes2)) {
			inputStream.transferTo(outputStream)
		} catch (IOException e) {
			throw new UncheckedIOException(e)
		}
	}

	@GetMapping("/result-buffer")
	void bufferResult(HttpServletResponse response) {
		ResponseUtils.writeBytesToResponse(Result.ok(null).toString().getBytes(StandardCharsets.UTF_8), response)
	}

	@GetMapping("/result")
	void result(HttpServletResponse response) {
		try (OutputStream outputStream = response.getOutputStream()) {
			InputStream inputStream = IOUtils.toUnsynchronizedByteArrayInputStream(
				Result.ok(null).toString().getBytes(StandardCharsets.UTF_8))
			inputStream.transferTo(outputStream)
		} catch (IOException e) {
			throw new UncheckedIOException(e)
		}
	}

	@GetMapping("/file-buffer")
	void fileBuffer(HttpServletResponse response) {
		ResponseUtils.writeFileToResponse(new File(
			"D:\\workspace\\project\\personal\\pangju-framework\\pangju-framework-web\\src\\test\\resources\\images\\test.jpg"),
			null, response)
	}

	@GetMapping("/file")
	void file(HttpServletResponse response) {
		ResponseUtils.writeFileToResponse(new File(
			"D:\\workspace\\project\\personal\\pangju-framework\\pangju-framework-web\\src\\test\\resources\\images\\test.jpg"),
			null, response, false)
	}
}