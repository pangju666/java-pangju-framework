package io.github.pangju666.framework.web.client


import io.github.pangju666.framework.web.TestApplication
import io.github.pangju666.framework.web.model.vo.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder
import spock.lang.Specification

@ActiveProfiles("test")
@ContextConfiguration(classes = TestApplication.class, loader = SpringBootContextLoader.class)
class ProxyRestClientUtilsSpec extends Specification {
	@Autowired
	RestClient restClient

	def "testBuildRequestBodySpec1"() {
		setup:
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://127.0.0.1/test-no-params")
		RestClientUtils.buildRequestBodySpec(restClient, builder, HttpMethod.GET,
			null, null, null, null)
			.retrieve()
			.toBodilessEntity()
	}

	def "testBuildRequestBodySpec2"() {
		given:
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://127.0.0.1")
			.path("/test-params/{path}")

		when:
		def requestBodySpec = RestClientUtils.buildRequestBodySpec(restClient, builder, HttpMethod.GET,
			null,
			new LinkedMultiValueMap<String, String>(Collections.singletonMap("param1", Collections.singletonList("test"))),
			Collections.singletonMap("path", "test"),
			null)
		def result = RestClientUtils.toJSONEntity(requestBodySpec,
			new ParameterizedTypeReference<Result<Map<String, String>>>() {}).getBody()

		then:
		result.data().get("param1") == "test"
		result.data().get("path") == "test"
	}
}
