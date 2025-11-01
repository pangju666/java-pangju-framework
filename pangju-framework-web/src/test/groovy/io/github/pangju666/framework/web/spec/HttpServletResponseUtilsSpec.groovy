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

import com.google.gson.Gson
import io.github.pangju666.commons.lang.utils.JsonUtils
import io.github.pangju666.framework.web.TestApplication
import io.github.pangju666.framework.web.client.RestClientHelper
import io.github.pangju666.framework.web.model.dto.ListDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.StopWatch
import org.springframework.web.client.RestClient
import spock.lang.Specification

@ActiveProfiles("test")
@ContextConfiguration(classes = TestApplication.class, loader = SpringBootContextLoader.class)
class HttpServletResponseUtilsSpec extends Specification {
	@Autowired
	RestClient restClient

	def "bytes"() {
		setup:
		StopWatch stopWatch = new StopWatch()
		stopWatch.start()

		def url = "http://127.0.0.1/response-utils/bytes"
		def result = RestClientHelper.fromUriString(restClient, url)
			.toBytesEntity()
			.getBody()

		stopWatch.stop()
		println stopWatch.lastTaskInfo().getTimeMillis()
	}

	def "bytes-buffer"() {
		setup:
		StopWatch stopWatch = new StopWatch()
		stopWatch.start()

		def url = "http://127.0.0.1/response-utils/bytes-buffer"
		def result = RestClientHelper.fromUriString(restClient, url)
			.toBytesEntity()
			.getBody()

		stopWatch.stop()
		println stopWatch.lastTaskInfo().getTimeMillis()
	}

	def "inputStream"() {
		setup:
		StopWatch stopWatch = new StopWatch()
		stopWatch.start()

		def url = "http://127.0.0.1/response-utils/inputStream"
		RestClientHelper.fromUriString(restClient, url)
			.toBytesEntity()
			.getBody()

		stopWatch.stop()
		println stopWatch.lastTaskInfo().getTimeMillis()
	}

	def "inputStream-buffer"() {
		setup:
		StopWatch stopWatch = new StopWatch()
		stopWatch.start()

		def url = "http://127.0.0.1/response-utils/inputStream-buffer"
		RestClientHelper.fromUriString(restClient, url)
			.toBytesEntity()
			.getBody()

		stopWatch.stop()
		println stopWatch.lastTaskInfo().getTimeMillis()
	}

	def "result"() {
		setup:
		StopWatch stopWatch = new StopWatch()
		stopWatch.start()

		def url = "http://127.0.0.1/response-utils/result"
		RestClientHelper.fromUriString(restClient, url)
			.toBytesEntity()
			.getBody()

		stopWatch.stop()
		println stopWatch.lastTaskInfo().getTimeMillis()
	}

	def "result-buffer"() {
		setup:
		StopWatch stopWatch = new StopWatch()
		stopWatch.start()

		def url = "http://127.0.0.1/response-utils/result-buffer"
		RestClientHelper.fromUriString(restClient, url)
			.toBytesEntity()
			.getBody()

		stopWatch.stop()
		println stopWatch.lastTaskInfo().getTimeMillis()
	}

	def "file"() {
		setup:
		StopWatch stopWatch = new StopWatch()
		stopWatch.start()

		def url = "http://127.0.0.1/response-utils/file"
		RestClientHelper.fromUriString(restClient, url)
			.toBytesEntity()
			.getBody()

		stopWatch.stop()
		println stopWatch.lastTaskInfo().getTimeMillis()
	}

	def "file-buffer"() {
		setup:
		StopWatch stopWatch = new StopWatch()
		stopWatch.start()

		def url = "http://127.0.0.1/response-utils/file-buffer"
		RestClientHelper.fromUriString(restClient, url)
			.toBytesEntity()
			.getBody()

		stopWatch.stop()
		println stopWatch.lastTaskInfo().getTimeMillis()
	}

	public enum TestEnum {
		TEST_1("测试枚举1"),
		TEST_2("测试枚举2"),
		TEST_3("测试枚举3");

		private String label;

		TestEnum(String label) {
			this.label = label;
		}

		public String getLabel() {
			return this.label;
		}
	}

	def "test"() {
		setup:
		Gson gson = JsonUtils.createGsonBuilder().setPrettyPrinting().create();

		List<String> list = Arrays.asList("1", "2", "3");
		ListDTO<String> listDTO = new ListDTO<>(list);
		println JsonUtils.toString(listDTO, gson)
	}
}
