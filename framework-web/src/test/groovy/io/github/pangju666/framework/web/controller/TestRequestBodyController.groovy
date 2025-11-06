package io.github.pangju666.framework.web.controller

import io.github.pangju666.framework.web.model.Result
import io.github.pangju666.framework.web.model.dto.TestDTO
import org.apache.commons.codec.binary.Base64
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RequestMapping
@RestController
class TestRequestBodyController {
	@GetMapping("/test-no-response")
	def testGet() {
		return new ResponseEntity<>(HttpStatus.OK)
	}

	@GetMapping("/test-a")
	def testGeta() {
		return Result.ok("测试")
	}

	@GetMapping("/test/{path}")
	def testGet(@PathVariable("path") String path,
				@RequestParam("test-param") String param,
				@RequestHeader("test-header") String header) {
		return Result.ok(List.of(path, param, header))
	}

	@PostMapping("/test-body/{path}")
	def testJsonRequestBody(@PathVariable("path") String path,
							@RequestParam("test-param") String param,
							@RequestHeader("test-header") String header,
							@RequestBody TestDTO testDTO) {
		return Result.ok(List.of(path, param, header, testDTO.key, testDTO.value))
	}

	@PostMapping("/test-text/{path}")
	def testTextRequestBody(@PathVariable("path") String path,
							@RequestParam("test-param") String param,
							@RequestHeader("test-header") String header,
							@RequestBody String text) {
		return Result.ok(List.of(path, param, header, text))
	}

	@PostMapping("/test-bytes/{path}")
	def testBytesRequestBody(@PathVariable("path") String path,
							 @RequestParam("test-param") String param,
							 @RequestHeader("test-header") String header,
							 @RequestBody byte[] bytes) {
		return Result.ok(List.of(path, param, header, Base64.encodeBase64String(bytes)))
	}

	@PostMapping(value = "/test-resource/{path}")
	def testResourceRequestBody(@PathVariable("path") String path,
								@RequestParam("test-param") String param,
								@RequestHeader("test-header") String header,
								@RequestBody Resource resource) {
		return Result.ok(List.of(path, param, header, resource.getInputStream().readAllBytes().size()))
	}

	@PostMapping(value = "/test-form/{path}")
	def testFormRequestBody(@PathVariable("path") String path,
							@RequestParam("test-param") String param,
							@RequestHeader("test-header") String header,
							@RequestPart("file") MultipartFile multipartFile) {
		return Result.ok(List.of(path, param, header, multipartFile.getSize()))
	}
}