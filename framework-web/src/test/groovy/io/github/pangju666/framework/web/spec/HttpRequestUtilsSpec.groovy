/*
 *    Copyright 2025 pangju666
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package io.github.pangju666.framework.web.spec

import io.github.pangju666.framework.web.TestApplication
import io.github.pangju666.framework.web.client.RestRequestBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestClient
import spock.lang.Specification

@ActiveProfiles("test")
@ContextConfiguration(classes = TestApplication.class, loader = SpringBootContextLoader.class)
class HttpRequestUtilsSpec extends Specification {
	@Autowired
	RestClient restClient

	def "getRequestUrl"() {
		setup:
		def url = "http://127.0.0.1:8080/test/request/headers"
		println RestRequestBuilder.fromUriString(restClient, url)
			.method(HttpMethod.POST)
			.formData("test", "test")
			.formPart("file", new ClassPathResource("images/test.jpg"))
			.toStringEntity()
			.getBody()
	}

	def "getRequestBody"() {
		setup:
		def url = "http://127.0.0.1:8080/test/request/body"
		println RestRequestBuilder.fromUriString(restClient, url)
			.method(HttpMethod.POST)
			.jsonBody(Collections.singletonMap("test", "value"))
			.toStringEntity()
			.getBody()
	}
}
