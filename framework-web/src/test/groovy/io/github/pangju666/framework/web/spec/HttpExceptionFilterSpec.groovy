package io.github.pangju666.framework.web.spec

import io.github.pangju666.framework.web.TestApplication
import io.github.pangju666.framework.web.client.builder.RestRequestBuilder
import io.github.pangju666.framework.web.model.common.Result
import io.github.pangju666.framework.web.model.vo.EnumVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestClient
import spock.lang.Specification

@ActiveProfiles("test")
@ContextConfiguration(classes = TestApplication.class, loader = SpringBootContextLoader.class)
class HttpExceptionFilterSpec extends Specification {
	@Autowired
	RestClient restClient

	def "types"() {
		setup:
		def list = RestRequestBuilder.fromUriString(restClient, "http://127.0.0.1/exception/types")
			.toJsonEntity(new ParameterizedTypeReference<Result<List<EnumVO>>>() {}).getBody()
		println list
	}

	/*def "list"() {
		setup:
		def list = RestClientHelper.fromUriString(restClient, "http://127.0.0.1/exception/list")
			.toJsonEntity(new ParameterizedTypeReference<Result<List<HttpExceptionVO>>>() {}).getBody()
		println list
	}*/
}