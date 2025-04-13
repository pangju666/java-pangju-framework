
package io.github.pangju666.framework.web.client.test;

import io.github.pangju666.commons.lang.utils.JsonUtils;
import io.github.pangju666.framework.web.client.RestClientHelper;
import io.github.pangju666.framework.web.client.model.dto.RequestXmlDTO;
import io.github.pangju666.framework.web.client.model.dto.TestDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.glassfish.jaxb.core.marshaller.XMLWriter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

@ActiveProfiles("test")
@SpringBootTest
public class XmlRestClientHelperTest {
	@Autowired
	RestClient restClient;

	@Test
	public void test1() {
		var result = RestClientHelper.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/test-xml-arr")
			.xmlBody(new TestDTO("body-key", "body-value"))
			.uriVariables(Collections.singletonMap("path", "test"))
			.toEntity(new ParameterizedTypeReference<Map<String, String>>() {})
			.getBody();
	}

	@Test
	public void test2() throws JAXBException {
		var result = RestClientHelper.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/test-xml-arr")
			.xmlBody(new TestDTO("body-key", "body-value"))
			.uriVariables(Collections.singletonMap("path", "test"))
			.toEntity(new ParameterizedTypeReference<Map<String, String>>() {})
			.getBody();
	}
}
