
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

import java.util.Collections;

@ActiveProfiles("test")
@SpringBootTest
public class TextRestClientHelperTest {
	@Autowired
	RestClient restClient;

	@Test
	public void testByBean() {
		var result = RestClientHelper.fromUriString(restClient, "http://localhost")
			.method(HttpMethod.POST)
			.path("/text/test-body")
			.textBody(new TestDTO("body-key", "body-value"))
			.queryParam("param1", "test")
			.uriVariables(Collections.singletonMap("path", "test"))
			.toStringEntity()
			.getBody();

		Assertions.assertNotNull(result);
		Assertions.assertEquals(new TestDTO("body-key", "body-value").toString(), result);
	}

	@Test
	public void testByStr() {
		var result = RestClientHelper.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/text/test-body")
			.textBody("text-body")
			.uriVariables(Collections.singletonMap("path", "test"))
			.toStringEntity()
			.getBody();

		Assertions.assertNotNull(result);
		Assertions.assertEquals("text-body", result);
	}

	@Test
	public void testByBytes() {
		var result = RestClientHelper.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/text/test-body")
			.textBody("text-body".getBytes())
			.uriVariables(Collections.singletonMap("path", "test"))
			.toStringEntity()
			.getBody();

		Assertions.assertNotNull(result);
		Assertions.assertEquals("text-body", result);
	}
}
