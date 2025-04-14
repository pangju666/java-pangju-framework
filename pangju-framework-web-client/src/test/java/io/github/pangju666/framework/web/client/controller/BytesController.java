package io.github.pangju666.framework.web.client.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/bytes")
@RestController
public class BytesController {
    @PostMapping(value = "/test-body")
    public ResponseEntity<Integer> testXml(@RequestBody byte[] body) {
        return ResponseEntity.ok(body.length);
    }
}