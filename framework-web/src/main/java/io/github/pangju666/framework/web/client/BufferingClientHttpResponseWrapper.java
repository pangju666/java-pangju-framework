/*
 *   Copyright 2025 pangju666
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.pangju666.framework.web.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@link ClientHttpResponse}的简单实现，可将响应的正文读入内存，从而允许多次调用{@link #getBody()}。
 *
 * <p>
 * 代码来自 org.springframework.http.client.BufferingClientHttpResponseWrapper
 * </p>
 *
 * @author Arjen Poutsma
 * @since 3.1
 */
public class BufferingClientHttpResponseWrapper implements ClientHttpResponse {
	private final ClientHttpResponse response;
	@Nullable
	private byte[] body;

	public BufferingClientHttpResponseWrapper(ClientHttpResponse response) {
		this.response = response;
	}

	@Override
	public HttpStatusCode getStatusCode() throws IOException {
		return this.response.getStatusCode();
	}

	@Override
	public String getStatusText() throws IOException {
		return this.response.getStatusText();
	}

	@Override
	public HttpHeaders getHeaders() {
		return this.response.getHeaders();
	}

	@Override
	public InputStream getBody() throws IOException {
		if (this.body == null) {
			this.body = StreamUtils.copyToByteArray(this.response.getBody());
		}
		return new ByteArrayInputStream(this.body);
	}

	@Override
	public void close() {
		this.response.close();
	}
}
