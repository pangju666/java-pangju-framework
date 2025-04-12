package io.github.pangju666.framework.web.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping
@RestController
class TestController {
	@GetMapping("/test")
	def testGet() {
		return "test"
	}

	@PostMapping("/test")
	def testPost() {
		return "test"
	}
}