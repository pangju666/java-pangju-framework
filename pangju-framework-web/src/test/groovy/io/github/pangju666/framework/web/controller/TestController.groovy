package io.github.pangju666.framework.web.controller

import io.github.pangju666.framework.web.model.dto.TestDTO
import io.github.pangju666.framework.web.model.vo.Result
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping
@RestController
class TestController {
	@GetMapping("/test-no-params")
	def testGet() {
		return new ResponseEntity<>(HttpStatus.OK)
	}

	@GetMapping("/test-params/{path}")
	def testPost(@PathVariable("path") String path, @RequestParam("param1") String param1) {
		return Result.ok(Map.of("path", path, "param1", param1))
	}

	@PostMapping("/test-body/{path}")
	def testPost(@PathVariable("path") String path, @RequestParam("param1") String param1, @RequestBody TestDTO testDTO) {
		return Result.ok(Map.of("path", path, "param1", param1, testDTO.getKey(), testDTO.getValue()))
	}
}