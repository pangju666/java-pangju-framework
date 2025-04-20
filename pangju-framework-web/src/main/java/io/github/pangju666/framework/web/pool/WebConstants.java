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

package io.github.pangju666.framework.web.pool;

/**
 * Web模块常量池
 *
 * @author pangju666
 * @since 1.0.0
 */
public class WebConstants {
	/**
	 * 管理员角色标识
	 * <p>
	 * 用于标识具有最高权限的管理员角色
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final String ADMIN_ROLE = "admin";

	/**
	 * 操作成功状态码
	 * <p>
	 * 表示业务操作执行成功的标准状态码
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final int SUCCESS_CODE = 0;
	/**
	 * 基础错误状态码
	 * <p>
	 * 表示发生通用错误时的基础状态码
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final int BASE_ERROR_CODE = -1;
	/**
	 * 默认成功响应消息
	 * <p>
	 * 当未指定具体成功消息时使用的标准提示文本
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final String DEFAULT_SUCCESS_MESSAGE = "请求成功";
	/**
	 * 默认失败响应消息
	 * <p>
	 * 当未指定具体错误消息时使用的标准提示文本
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final String DEFAULT_FAILURE_MESSAGE = "请求失败";

	/**
	 * 通用路径匹配模式常量
	 * <p>
	 * 表示匹配所有路径及其子路径的Ant风格路径模式。
	 * 该常量可用于拦截器、过滤器等组件中设置通配路径，
	 * 例如在配置拦截器时使用此常量可匹配应用中的所有请求路径。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final String ANY_PATH_PATTERN = "/**";

	/**
	 * 本地主机IP常量
	 * <p>
	 * 表示本地回环地址(loopback address)的标准IPv4地址，常用于本地开发和测试环境。
	 * 所有发往此地址的网络请求都不会被发送到网络接口，而是在本地处理。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final String LOCAL_HOST_IP = "127.0.0.1";
	/**
	 * 本地主机名常量
	 * <p>
	 * 表示本地回环地址的标准主机名，等同于使用IP地址 {@link #LOCAL_HOST_IP}。
	 * 在网络编程中，通常可以互换使用"localhost"和"127.0.0.1"。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final String LOCAL_HOST_NAME = "localhost";
	/**
	 * 未知地址标识
	 *
	 * @since 1.0.0
	 */
	public static final String UNKNOWN_ADDRESS = "unknown";

	protected WebConstants() {
	}
}
