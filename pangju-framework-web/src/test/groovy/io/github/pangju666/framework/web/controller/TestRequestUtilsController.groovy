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

import com.google.gson.reflect.TypeToken
import io.github.pangju666.framework.web.utils.HttpRequestUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.Part
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RequestMapping("/request")
@RestController
class TestRequestUtilsController {
	@PostMapping("/headers")
	ResponseEntity<Map<String, Object>> testGet(HttpServletRequest request,
												@RequestPart("test") String param,
												@RequestPart("file") MultipartFile multipartFile) {
		def partMap = HttpRequestUtils.getRequestParts(request)
		Map<String, Object> map = new HashMap<>(partMap.size())
		for (final def entry in map.entrySet()) {
			Part part = entry.getValue() as Part
			Map<String, Object> multipartFileMap = new HashMap<>(3);
			multipartFileMap.put("contentType", part.getContentType());
			multipartFileMap.put("filename", part.getSubmittedFileName());
			multipartFileMap.put("size", part.getSize());
			map.put(entry.getKey(), multipartFileMap)
		}
		return ResponseEntity.ok(map)
	}

	@PostMapping("/body")
	ResponseEntity<Map<String, Object>> testGet(HttpServletRequest request) {
		return ResponseEntity.ok(HttpRequestUtils.getJsonRequestBody(request, new TypeToken<Map<String, Object>>() {
		}))
	}
}