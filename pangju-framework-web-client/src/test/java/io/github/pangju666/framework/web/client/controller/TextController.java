package io.github.pangju666.framework.web.client.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/text")
@RestController
public class TextController {
	@PostMapping(value = "/test-body")
	public ResponseEntity<String> testXml(@RequestBody String body) {
		return ResponseEntity.ok(body);
	}
}