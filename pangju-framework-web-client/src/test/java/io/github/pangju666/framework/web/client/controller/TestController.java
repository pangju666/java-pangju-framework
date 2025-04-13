package io.github.pangju666.framework.web.client.controller;

import io.github.pangju666.framework.web.client.model.dto.RequestXmlDTO;
import io.github.pangju666.framework.web.client.model.dto.TestDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping
@RestController
public class TestController {
	@GetMapping("/test-no-params")
	public ResponseEntity<Void> testNoParams() {
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/test-params/{path}")
	public ResponseEntity<Map<String, String>> testParams(@PathVariable("path") String path, @RequestParam("param1") String param1) {
		return ResponseEntity.ok(Map.of("path", path, "param1", param1));
	}

	@PostMapping("/test-body/{path}")
	public ResponseEntity<Map<String, String>> testBody(@PathVariable("path") String path, @RequestParam("param1") String param1, @RequestBody TestDTO testDTO) {
		return ResponseEntity.ok(Map.of("path", path, "param1", param1, testDTO.getKey(), testDTO.getValue()));
	}

	@PostMapping("/test-list")
	public ResponseEntity<String> testStr(@RequestBody List<String> list) {
		return ResponseEntity.ok(StringUtils.join(list, ","));
	}

	@PostMapping("/test-arr")
	public ResponseEntity<String> testStr(@RequestBody String[] list) {
		return ResponseEntity.ok(StringUtils.join(list, ","));
	}

	@PostMapping(value = "/test-xml-arr", consumes = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<Map<String, String>> testXml(@RequestBody RequestXmlDTO testDTO) {
		return ResponseEntity.ok(Map.of(testDTO.getKey(), testDTO.getValue()));
	}
}