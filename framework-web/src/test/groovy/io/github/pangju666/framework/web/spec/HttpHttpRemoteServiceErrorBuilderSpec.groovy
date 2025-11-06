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


import io.github.pangju666.framework.web.model.error.HttpRemoteServiceError
import org.springframework.http.HttpStatus
import spock.lang.Specification

class HttpHttpRemoteServiceErrorBuilderSpec extends Specification {
	/*def "构造函数应正确初始化基本属性"() {
		when: "创建构建器实例"
		def builder = new HttpRemoteServiceErrorBuilder("测试服务", "测试接口")

		then: "基本属性应被正确初始化"
		builder.build().with {
			service() == "测试服务"
			api() == "测试接口"
			httpStatus().value() == HttpStatus.OK.value()
			message() == null
			code() == null
			uri() == null
		}
	}

	def "带URI的构造函数应正确初始化所有属性"() {
		given: "准备测试URI"
		def uri = new URI("http://test.com/api")

		when: "创建带URI的构建器实例"
		def builder = new HttpRemoteServiceErrorBuilder("测试服务", "测试接口")
			.uri(uri)

		then: "所有属性应被正确初始化"
		builder.build().with {
			service() == "测试服务"
			api() == "测试接口"
			it.uri() == uri
			httpStatus().value() == HttpStatus.OK.value()
			message() == null
			code() == null
		}
	}

	def "message方法应正确设置错误消息"() {
		given: "创建构建器实例"
		def builder = new HttpRemoteServiceErrorBuilder("测试服务", "测试接口")

		when: "设置错误消息"
		builder.message("测试错误")

		then: "错误消息应被正确设置"
		builder.build().message() == "测试错误"
	}

	@Unroll
	def "message方法应正确处理带参数的消息模板：#pattern"() {
		given: "创建构建器实例"
		def builder = new HttpRemoteServiceErrorBuilder("测试服务", "测试接口")

		when: "使用模板设置错误消息"
		builder.message(pattern, args as Object[])

		then: "错误消息应被正确格式化"
		builder.build().message() == expected

		where:
		pattern         | args      | expected
		"用户{0}不存在" | ["admin"] | "用户admin不存在"
		"错误代码：{0}"  | [404]     | "错误代码：404"
		""              | []        | null
		null            | []        | null
	}

	@Unroll
	def "code方法应正确设置错误代码：#inputCode"() {
		given: "创建构建器实例"
		def builder = new HttpRemoteServiceErrorBuilder("测试服务", "测试接口")

		when: "设置错误代码"
		builder.code(inputCode)

		then: "错误代码应被正确设置"
		builder.build().code() == expected

		where:
		inputCode | expected
		404       | "404"
		"E001"    | "E001"
		null      | null
	}

	def "httpStatus方法应正确设置HTTP状态码"() {
		given: "创建构建器实例"
		def builder = new HttpRemoteServiceErrorBuilder("测试服务", "测试接口")

		when: "设置HTTP状态码"
		builder.httpStatus(HttpStatus.NOT_FOUND)

		then: "HTTP状态码应被正确设置"
		builder.build().httpStatus().value() == HttpStatus.NOT_FOUND.value()
	}

	def "buildException应正确处理网关超时异常"() {
		given: "创建构建器实例和网关超时异常"
		def builder = new HttpRemoteServiceErrorBuilder("测试服务", "测试接口")
		def exception = HttpServerErrorException.create(HttpStatus.GATEWAY_TIMEOUT,
			"Gateway Timeout", HttpHeaders.EMPTY, ArrayUtils.EMPTY_BYTE_ARRAY, null)

		when: "构建异常"
		def result = builder.buildException(exception, "code", "message")

		then: "应返回超时异常"
		result instanceof HttpRemoteServiceTimeoutException
		result.getError().httpStatus().value() == HttpStatus.GATEWAY_TIMEOUT.value()
	}

	def "buildException应正确处理响应异常"() {
		given: "创建构建器实例和模拟响应数据"
		def builder = new HttpRemoteServiceErrorBuilder("测试服务", "测试接口")
		def responseBody = new JsonObject()
		responseBody.addProperty("code", "E001")
		responseBody.addProperty("message", "业务错误")
		def exception = new RestClientResponseException(
			"测试异常",
			HttpStatus.valueOf(400),
			"Bad Request",
			null,
			responseBody.toString().bytes,
			null
		)

		when: "构建异常"
		def result = builder.buildException(exception, "code", "message")

		then: "应正确解析响应体信息"
		result instanceof HttpRemoteServiceException
		with(result.getError()) {
			code() == "E001"
			message() == "业务错误"
			httpStatus().value() == 400
		}
	}

	def "buildException不应接受null参数"() {
		given: "创建构建器实例"
		def builder = new HttpRemoteServiceErrorBuilder("测试服务", "测试接口")

		when: "传入null异常"
		builder.buildException(null, "code", "message")

		then: "应抛出参数异常"
		thrown(IllegalArgumentException)
	}*/

	def "ada"() {
		setup:
		HttpRemoteServiceError error = new HttpRemoteServiceError(
			"用户服务",                    // 服务名称
			"获取用户信息",                // 接口名称
			new URI("http://api.example.com/users"), // 请求URI
			"USER-404",                   // 错误码
			"用户不存在",                   // 错误消息
			HttpStatus.NOT_FOUND          // HTTP状态码
		);
	}
}