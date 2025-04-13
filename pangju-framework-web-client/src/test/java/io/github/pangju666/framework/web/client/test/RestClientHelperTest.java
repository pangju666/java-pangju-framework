package io.github.pangju666.framework.web.client.test;

import io.github.pangju666.framework.web.client.RestClientHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestClient;

import java.util.Map;

@ActiveProfiles("test")
@SpringBootTest
public class RestClientHelperTest {
	@Autowired
	RestClient restClient;

	@Test
	public void test1() {
		var entity = RestClientHelper.fromUriString(restClient, "http://127.0.0.1/test-no-params")
			.method(HttpMethod.GET)
			.toBodilessEntity();

		Assertions.assertNull(entity.getBody());
		Assertions.assertEquals(HttpStatus.OK, entity.getStatusCode());
	}

	@Test
	public void test2() {
		var data = RestClientHelper.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.GET)
			.path("/test-params/{path}")
			.queryParam("param1", "test")
			.uriVariable("path", "test")
			.toEntity(new ParameterizedTypeReference<Map<String, String>>() {})
			.getBody();

		Assertions.assertEquals("test", data.get("param1"));
		Assertions.assertEquals("test", data.get("path"));
	}
}
