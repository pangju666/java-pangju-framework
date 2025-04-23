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

import io.github.pangju666.commons.io.utils.FileUtils
import io.github.pangju666.framework.web.TestApplication
import io.github.pangju666.framework.web.controller.TestFileResponseUtilsController
import org.apache.commons.lang3.ArrayUtils
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.util.StopWatch
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@ActiveProfiles("test")
@ContextConfiguration(classes = TestApplication.class, loader = SpringBootContextLoader.class)
class FileResponseUtilsSpec extends Specification {
	def file = new File("D:\\workspace\\resource\\图片\\lADPDg7mUhel9rHMtsyy_178_182.jpg")
	def bytes = FileUtils.readFileToByteArray(file)

	MockMvc mockMvc

	def setup() {
		mockMvc = standaloneSetup(TestFileResponseUtilsController)
			.build()
	}

	def "error-range"() {
		setup:
		mockMvc.perform(get(URI.create("/file-response-utils/file"))
			.header(HttpHeaders.RANGE, "bytes="))
			.andExpect(status().is(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value()))

		mockMvc.perform(get(URI.create("/file-response-utils/file"))
			.header(HttpHeaders.RANGE, "bytes=,"))
			.andExpect(status().is(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value()))

		mockMvc.perform(get(URI.create("/file-response-utils/file"))
			.header(HttpHeaders.RANGE, "bytes=-"))
			.andExpect(status().is(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value()))

		mockMvc.perform(get(URI.create("/file-response-utils/file"))
			.header(HttpHeaders.RANGE, "bytes=-1-"))
			.andExpect(status().is(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value()))

		mockMvc.perform(get(URI.create("/file-response-utils/file"))
			.header(HttpHeaders.RANGE, "bytes=5000000000000000-"))
			.andExpect(status().is(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value()))

		mockMvc.perform(get(URI.create("/file-response-utils/file"))
			.header(HttpHeaders.RANGE, "bytes=0-5000000000000000"))
			.andExpect(status().is(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value()))

		mockMvc.perform(get(URI.create("/file-response-utils/file"))
			.header(HttpHeaders.RANGE, "bytes=0-5000000000000000,5000--100000"))
			.andExpect(status().is(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value()))

		mockMvc.perform(get(URI.create("/file-response-utils/file"))
			.header(HttpHeaders.RANGE, "bytes=1000-100"))
			.andExpect(status().is(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value()))
	}

	def "complete-range"() {
		setup:
		mockMvc.perform(get(URI.create("/file-response-utils/file"))
			.header(HttpHeaders.RANGE, ""))
			.andExpect(status().isOk())
			.andExpect(content().bytes(bytes))

		mockMvc.perform(get(URI.create("/file-response-utils/file")))
			.andExpect(status().isOk())
			.andExpect(content().bytes(bytes))

		mockMvc.perform(get(URI.create("/file-response-utils/file"))
			.header(HttpHeaders.RANGE, "bytes=0-${bytes.length - 1}"))
			.andExpect(status().isOk())
			.andExpect(content().bytes(bytes))

		mockMvc.perform(get(URI.create("/file-response-utils/file"))
			.header(HttpHeaders.RANGE, "bytes=0-"))
			.andExpect(status().isOk())
			.andExpect(content().bytes(bytes))
	}

	def "single-range"() {
		setup:
		mockMvc.perform(get(URI.create("/file-response-utils/file"))
			.header(HttpHeaders.RANGE, "bytes=0-99"))
			.andExpect(status().is(HttpStatus.PARTIAL_CONTENT.value()))
			.andExpect(content().bytes(ArrayUtils.subarray(bytes, 0, 100)))

		StopWatch stopWatch = new StopWatch()
		stopWatch.start()

		mockMvc.perform(get(URI.create("/file-response-utils/file"))
			.header(HttpHeaders.RANGE, "bytes=100-200"))
			.andExpect(status().is(HttpStatus.PARTIAL_CONTENT.value()))
			.andExpect(content().bytes(ArrayUtils.subarray(bytes, 100, 201)))

		stopWatch.stop()
		println stopWatch.lastTaskInfo().getTimeMillis()
	}

	def "multi-range"() {
		when:
		byte[] result1 = mockMvc.perform(get(URI.create("/file-response-utils/file"))
			.header(HttpHeaders.RANGE, "bytes=0-99,100-"))
			.andExpect(status().is(HttpStatus.PARTIAL_CONTENT.value()))
			.andReturn()
			.getResponse()
			.getContentAsByteArray()
		def lines1 = new String(result1).split("\r\n")

		/*byte[] result2 = mockMvc.perform(get(URI.create("/file-response-utils/file"))
			.header(HttpHeaders.RANGE, "bytes=0-99, 100-"))
			.andExpect(status().is(HttpStatus.PARTIAL_CONTENT.value()))
			.andReturn()
			.getResponse()
			.getContentAsByteArray()
		def lines2 = new String(result2).split("\r\n")*/

		then:
		//result1 == result2
		(lines1[4].getBytes() + lines1[9].getBytes()) == bytes
		//(lines2[4].getBytes() + lines2[9].getBytes()) == bytes
	}
}
