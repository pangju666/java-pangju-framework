package io.github.pangju666.framework.web.client;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * 响应体缓冲拦截器
 * <p>
 * 通过将底层 {@link ClientHttpResponse} 包装为
 * {@link BufferingClientHttpResponseWrapper}，
 * 缓存响应体数据以支持对响应体的重复读取（例如日志打印、错误处理器解析等）。
 * 该拦截器本身无状态，适用于在多线程环境下复用。
 * </p>
 *
 * <p>
 * 使用场景：
 * 当后续处理（如 {@code ResponseErrorHandler} 或其他拦截器）需要多次读取响应体时，
 * 在客户端构建阶段注册本拦截器即可避免一次性流被消费后无法再次读取的问题。
 * </p>
 *
 * @author pangju666
 * @see ClientHttpRequestInterceptor
 * @see ClientHttpResponse
 * @see BufferingClientHttpResponseWrapper
 * @since 1.0.0
 */
public class BufferingResponseInterceptor implements ClientHttpRequestInterceptor {
	/**
	 * 拦截请求并将响应包装为可重复读取的缓冲响应
	 * <p>
	 * 委托执行实际请求后，返回被 {@link BufferingClientHttpResponseWrapper} 包装的响应，
	 * 使响应体可被后续处理逻辑重复读取。
	 * </p>
	 *
	 * @param request   原始 HTTP 请求
	 * @param body      请求体字节数组
	 * @param execution 执行器，用于实际发送请求
	 * @return 被缓冲包装的 {@link ClientHttpResponse}
	 * @throws IOException 执行请求或读取响应时的 IO 异常
	 * @since 1.0.0
	 */
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		return new BufferingClientHttpResponseWrapper(execution.execute(request, body));
	}
}
