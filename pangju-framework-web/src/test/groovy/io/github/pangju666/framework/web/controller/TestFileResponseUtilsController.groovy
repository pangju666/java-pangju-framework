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


import io.github.pangju666.framework.web.utils.RangeDownloadUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/file-response-utils")
@RestController
class TestFileResponseUtilsController {
	def file = new File("D:\\workspace\\resource\\图片\\郑和航海图.jpg")

	TestFileResponseUtilsController() {

	}

	@GetMapping("/file")
	void file(HttpServletRequest request, HttpServletResponse response) {
		RangeDownloadUtils.downloadFile(file, request, response)
	}
}