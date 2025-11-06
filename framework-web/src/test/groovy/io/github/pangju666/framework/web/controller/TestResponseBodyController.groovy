package io.github.pangju666.framework.web.controller


import io.github.pangju666.framework.web.model.Result
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

@RequestMapping("/response")
@RestController
class TestResponseBodyController {
	@GetMapping("/json")
	Result<String> testJson() {
		return Result.ok("json")
	}

	@GetMapping("/str")
	@ResponseBody
	String testString() {
		return "string"
	}

	@GetMapping("/bytes")
	byte[] testBytes() {
		return "bytes".getBytes()
	}

	@GetMapping("/resource")
	Resource testResource() {
		return new ClassPathResource("images/test.jpg")
	}

	@GetMapping("/image")
	BufferedImage testImage() {
		try (def inputStream = new ClassPathResource("images/test.jpg").getInputStream()) {
			return ImageIO.read(inputStream)
		}
	}
}