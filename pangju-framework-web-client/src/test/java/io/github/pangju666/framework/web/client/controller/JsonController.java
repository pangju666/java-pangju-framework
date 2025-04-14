package io.github.pangju666.framework.web.client.controller;

import io.github.pangju666.framework.web.client.model.dto.TestDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequestMapping
@RestController("/json")
public class JsonController {
	@PostMapping("/test-body")
	public ResponseEntity<Map<String, String>> testBody(@RequestBody TestDTO testDTO) {
		return ResponseEntity.ok(Map.of(testDTO.getKey(), testDTO.getValue()));
	}

	@PostMapping("/test-list")
	public ResponseEntity<String> testList(@RequestBody List<String> list) {
		return ResponseEntity.ok(StringUtils.join(list, ","));
	}

	@PostMapping("/test-arr")
	public ResponseEntity<String> testArr(@RequestBody String[] list) {
		return ResponseEntity.ok(StringUtils.join(list, ","));
	}
}