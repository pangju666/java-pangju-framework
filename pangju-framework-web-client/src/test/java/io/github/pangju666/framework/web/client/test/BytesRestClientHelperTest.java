
package io.github.pangju666.framework.web.client.test;

import io.github.pangju666.framework.web.client.RestClientHelper;
import io.github.pangju666.framework.web.client.model.dto.TestDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Collections;

@ActiveProfiles("test")
@SpringBootTest
public class BytesRestClientHelperTest {
	@Autowired
	RestClient restClient;

	@Test
	public void testByBean() {
		var result = RestClientHelper.fromUriString(restClient, "http://localhost")
			.method(HttpMethod.POST)
			.path("/bytes/test-body")
			.bytesBody(new TestDTO("body-key", "body-value"))
			.queryParam("param1", "test")
			.uriVariables(Collections.singletonMap("path", "test"))
			.toEntity(Integer.class)
			.getBody();

		Assertions.assertNotNull(result);
		Assertions.assertEquals(new TestDTO("body-key", "body-value").toString().getBytes().length, result);
	}

	@Test
	public void testByStr() {
		var result = RestClientHelper.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/bytes/test-body")
			.bytesBody("text-body")
			.uriVariables(Collections.singletonMap("path", "test"))
			.toEntity(Integer.class)
			.getBody();

		Assertions.assertNotNull(result);
		Assertions.assertEquals("text-body".getBytes().length, result);
	}

	@Test
	public void testByBytes() {
		var result = RestClientHelper.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/bytes/test-body")
			.bytesBody("text-body".getBytes())
			.uriVariables(Collections.singletonMap("path", "test"))
			.toEntity(Integer.class)
			.getBody();

		Assertions.assertNotNull(result);
		Assertions.assertEquals("text-body".getBytes().length, result);
	}

	@Test
	public void testByInputStream() {
		var result = RestClientHelper.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/bytes/test-body")
			.bytesBody(new ByteArrayInputStream("text-body".getBytes()))
			.uriVariables(Collections.singletonMap("path", "test"))
			.toEntity(Integer.class)
			.getBody();

		Assertions.assertNotNull(result);
		Assertions.assertEquals("text-body".getBytes().length, result);
	}

	@Test
	public void testByFile() {
		var result = RestClientHelper.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/bytes/test-body")
			.bytesBody(new File("src/test/resources/application-test.yml"))
			.uriVariables(Collections.singletonMap("path", "test"))
			.toEntity(Long.class)
			.getBody();

		Assertions.assertNotNull(result);
		Assertions.assertEquals(new File("src/test/resources/application-test.yml").length(), result);
	}
}
