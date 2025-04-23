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

import io.github.pangju666.framework.web.exception.base.ServiceException
import io.github.pangju666.framework.web.model.common.Result
import io.github.pangju666.framework.web.utils.FileResponseUtils
import io.github.pangju666.framework.web.utils.ResponseUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import java.nio.charset.StandardCharsets

@RequestMapping("/response-utils")
@RestController
class TestResponseUtilsController {
	@GetMapping("/stream")
	void stream(HttpServletResponse response) {
		ResponseUtils.writeInputStreamToResponse(new ByteArrayInputStream(Result.ok("测试").toString().getBytes(StandardCharsets.UTF_8)), response)
	}

	@GetMapping("/bytes")
	void bytes(HttpServletResponse response) {
		ResponseUtils.writeBytesToResponse(Result.ok("测试").toString().getBytes(StandardCharsets.UTF_8), response)
	}

	@GetMapping("/result")
	void result(HttpServletResponse response) {
		ResponseUtils.writeResultToResponse(Result.ok("测试"), response)
	}

	@GetMapping("/exception")
	void exception(HttpServletResponse response) {
		ResponseUtils.writeHttpExceptionToResponse(new ServiceException("测试异常"), response)
	}

	@GetMapping("/file")
	void file(HttpServletRequest request, HttpServletResponse response) {
		FileResponseUtils.writeFileToResponse(new File(
			"D:\\workspace\\project\\personal\\pangju-framework\\pangju-framework-web\\src\\test\\resources\\images\\test.jpg"),
			null, MediaType.IMAGE_JPEG_VALUE, request, response)
	}
}