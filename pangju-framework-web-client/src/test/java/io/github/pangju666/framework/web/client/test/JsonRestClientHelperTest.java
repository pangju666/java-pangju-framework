package io.github.pangju666.framework.web.client.test;

import io.github.pangju666.commons.lang.utils.JsonUtils;
import io.github.pangju666.framework.web.client.RestClientHelper;
import io.github.pangju666.framework.web.client.model.dto.TestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

@ActiveProfiles("test")
@SpringBootTest
public class JsonRestClientHelperTest {
	@Autowired
	RestClient restClient;

	@Test
	public void test3() {
		var result = RestClientHelper.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/test-body/{path}")
			.queryParam("param1", "test")
			.jsonBody(new TestDTO("body-key", "body-value"))
			.uriVariable("path", "test")
			.toEntity(new ParameterizedTypeReference<Map<String, String>>() {})
			.getBody();

		/*then:
		result.get("param1") == "test"
		result.get("path") == "test"
		result.get("body-key") == "body-value"*/
	}

	@Test
	public void test4() {
		var result = RestClientHelper.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/test-body/{path}")
			.queryParams(Collections.singletonMap("param1", "test"))
			.jsonBody(JsonUtils.toJson(new TestDTO("body-key", "body-value")))
			.uriVariables(Collections.singletonMap("path", "test"))
			.toEntity(new ParameterizedTypeReference<Map<String, String>>() {})
			.getBody();

		/*then:
		result.get("param1") == "test"
		result.get("path") == "test"
		result.get("body-key") == "body-value"*/
	}

	@Test
	public void test5() {

		var result = RestClientHelper.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/test-body/{path}")
			.queryParams(Collections.singletonMap("param1", "test"))
			.jsonBody("{ \"key\": \"body-key\", \"value\": \"body-value\" }")
			.uriVariables(Collections.singletonMap("path", "test"))
			.toEntity(new ParameterizedTypeReference<Map<String, String>>() {})
			.getBody();

		/*then:
		result.get("param1") == "test"
		result.get("path") == "test"
		result.get("body-key") == "body-value"*/
	}

	@Test
	public void test6() {
		var result = RestClientHelper.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/test-body/{path}")
			.queryParams(Collections.singletonMap("param1", "test"))
			.jsonBody("{ \"key\": \"body-key\", \"value\": \"body-value\" }".getBytes(StandardCharsets.UTF_8))
			.uriVariables(Collections.singletonMap("path", "test"))
			.toEntity(new ParameterizedTypeReference<Map<String, String>>() {})
			.getBody();

		/*then:
		result.get("param1") == "test"
		result.get("path") == "test"
		result.get("body-key") == "body-value"*/
	}

	@Test
	public void test7() {
		var result = RestClientHelper.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/test-list")
			.jsonBody("[\"test\"]")
			.uriVariables(Collections.singletonMap("path", "test"))
			.toEntity(String.class)
			.getBody();

		/*then:
		result == "test"*/
	}

	@Test
	public void test8() {
		var result = RestClientHelper.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/test-arr")
			.jsonBody("[\"test\"]")
			.uriVariables(Collections.singletonMap("path", "test"))
			.toEntity(String.class)
			.getBody();

		/*then:
		result == "test"*/
	}
}
