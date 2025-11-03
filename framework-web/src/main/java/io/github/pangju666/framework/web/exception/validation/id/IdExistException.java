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

package io.github.pangju666.framework.web.exception.validation.id;

import io.github.pangju666.framework.web.annotation.HttpException;
import io.github.pangju666.framework.web.enums.HttpExceptionType;
import io.github.pangju666.framework.web.exception.base.ValidationException;
import org.springframework.http.HttpStatus;

/**
 * ID已存在异常
 * <p>
 * 用于处理ID重复的情况，如：
 * <ul>
 *     <li>插入记录时ID冲突</li>
 *     <li>批量导入时存在重复ID</li>
 *     <li>更新时违反主键约束</li>
 * </ul>
 * </p>
 *
 * <p>
 * 特点：
 * <ul>
 *     <li>错误码：-4210（{@link HttpExceptionType#VALIDATION} + 210）</li>
 *     <li>HTTP状态码：400（{@link HttpStatus#BAD_REQUEST}）</li>
 *     <li>不记录日志</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@HttpException(code = 210, description = "id已存在错误", type = HttpExceptionType.VALIDATION,
	log = false, status = HttpStatus.BAD_REQUEST)
public class IdExistException extends ValidationException {
	/**
	 * 创建ID已存在异常实例（使用默认错误消息）
	 *
	 * @since 1.0.0
	 */
	public IdExistException() {
		super("id已存在");
	}

	/**
	 * 创建ID已存在异常实例（使用自定义错误消息）
	 *
	 * @param message 自定义错误消息
	 * @since 1.0.0
	 */
	public IdExistException(String message) {
		super(message);
	}
}