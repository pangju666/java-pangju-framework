package io.github.pangju666.framework.web.client.controller;

import io.github.pangju666.framework.web.client.model.dto.RequestXmlDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping
@RestController("/xml")
public class XmlController {
	@PostMapping(value = "/test-body", consumes = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<Map<String, String>> testXml(@RequestBody RequestXmlDTO testDTO) {
		return ResponseEntity.ok(Map.of(testDTO.getKey(), testDTO.getValue()));
	}
}