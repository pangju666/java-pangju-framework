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
 * <p>
 * 定义Web模块中使用的各类常量，包括：
 * <ul>
 *     <li>角色权限常量</li>
 *     <li>错误码常量</li>
 * </ul>
 * </p>
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

	protected WebConstants() {
	}
}
