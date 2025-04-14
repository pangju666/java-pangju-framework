package io.github.pangju666.framework.web.client.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/form-data")
@RestController
public class FormDataController {
	@PostMapping(value = "/test-part")
	public ResponseEntity<Long> testXml(@RequestPart("file") MultipartFile multipartFile) {
		return ResponseEntity.ok(multipartFile.getSize());
	}
}