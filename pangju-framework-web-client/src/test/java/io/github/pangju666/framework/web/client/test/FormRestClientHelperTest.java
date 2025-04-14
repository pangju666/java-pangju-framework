
package io.github.pangju666.framework.web.client.test;

import io.github.pangju666.framework.web.client.RestClientHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.util.Collections;

@ActiveProfiles("test")
@SpringBootTest
public class FormRestClientHelperTest {
	@Autowired
	RestClient restClient;

	@Test
	public void testByFile() {
		var result = RestClientHelper.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/form-data/test-part")
			.fromDataPart("file", new File("src/test/resources/application-test.yml"))
			.uriVariables(Collections.singletonMap("path", "test"))
			.toEntity(Long.class)
			.getBody();

		Assertions.assertNotNull(result);
		Assertions.assertEquals(new File("src/test/resources/application-test.yml").length(), result);
	}

}
