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

package io.github.pangju666.framework.web.annotation;

import io.github.pangju666.framework.web.enums.HttpExceptionType;
import io.github.pangju666.framework.web.exception.base.BaseHttpException;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

import java.lang.annotation.*;

/**
 * HTTP异常配置注解
 * <p>
 * 用于配置{@link BaseHttpException}子类的HTTP响应行为，包括错误码、异常类型、描述信息、
 * 日志记录行为和HTTP状态码等。配合{@link HttpExceptionType}使用，实现统一的异常管理。
 * </p>
 *
 * <p>
 * 错误码计算规则：
 * <ul>
 *     <li>最终错误码 = -(基础码 + |配置码|)</li>
 *     <li>基础码来自{@link HttpExceptionType}枚举值</li>
 *     <li>配置码大于1000时，仅保留后三位（如1234变为234）</li>
 *     <li>使用负数表示错误状态</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 定义业务异常
 * @HttpException(
 *     code = 234,
 *     type = HttpExceptionType.SERVICE,
 *     description = "用户余额不足",
 *     status = HttpStatus.BAD_REQUEST
 * )
 * public class InsufficientBalanceException extends ServiceException {
 *     // 最终错误码：-1234（SERVICE基础码1000 + 配置码234）
 *     public InsufficientBalanceException(String userId) {
 *         super("余额不足，无法完成操作", "用户ID: " + userId);
 *     }
 * }
 *
 * // 定义验证异常（不记录日志）
 * @HttpException(
 *     code = 56,
 *     type = HttpExceptionType.VALIDATION,
 *     description = "参数格式错误",
 *     log = false
 * )
 * public class InvalidParameterException extends ValidationException {
 *     // 最终错误码：-4056（VALIDATION基础码4000 + 配置码56）
 * }
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @see BaseHttpException
 * @see HttpExceptionType
 * @since 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
public @interface HttpException {
	/**
	 * 异常配置码
	 * <p>
	 * 与异常类型基础码组合生成最终错误码：
	 * <ul>
	 *     <li>配置码的绝对值参与计算</li>
	 *     <li>配置码大于1000时，仅保留后三位</li>
	 *     <li>计算公式：-(基础码 + |配置码|)</li>
	 * </ul>
	 * </p>
	 *
	 * <p>
	 * 示例：
	 * <ul>
	 *     <li>SERVICE(1000) + code(1) = -1001</li>
	 *     <li>DATA_OPERATION(2000) + code(1234) = -2234（保留234）</li>
	 *     <li>VALIDATION(4000) + code(-56) = -4056（取绝对值）</li>
	 * </ul>
	 * </p>
	 *
	 * @return 异常配置码
	 * @since 1.0.0
	 */
	int code();

	/**
	 * 异常类型
	 * <p>
	 * 用于分类异常，提供对应的基础错误码。<br />
	 * 可选值参见{@link HttpExceptionType}枚举。
	 * </p>
	 *
	 * @return 异常类型，默认为{@link HttpExceptionType#CUSTOM}
	 * @since 1.0.0
	 */
	HttpExceptionType type() default HttpExceptionType.CUSTOM;

	/**
	 * 异常概述
	 * <p>
	 * 用于简要描述异常的具体含义，如：
	 * <ul>
	 *     <li>用户相关：用户未找到、用户未登录、用户无权限等</li>
	 *     <li>数据相关：数据不存在、数据格式错误、数据重复等</li>
	 *     <li>业务相关：余额不足、库存不足、订单已关闭等</li>
	 *     <li>系统相关：服务器内部错误、服务调用超时、数据库连接失败等</li>
	 * </ul>
	 * 默认为空字符串。建议使用简洁明确的描述，便于理解异常的具体场景。
	 * </p>
	 *
	 * @return 异常的概述说明
	 * @since 1.0.0
	 */
	String description() default "";

	/**
	 * 是否记录日志
	 * <p>
	 * 控制是否记录异常日志。对于预期内的业务异常（如参数验证失败），
	 * 可设置为false以减少日志量。
	 * </p>
	 *
	 * @return 是否记录日志，默认为true
	 * @since 1.0.0
	 */
	boolean log() default true;

	/**
	 * 日志级别
	 * <p>
	 * 设置异常的日志记录级别。建议根据异常严重程度选择：
	 * <ul>
	 *     <li>ERROR：系统错误、数据异常等严重问题</li>
	 *     <li>WARN：业务规则违反、权限不足等警告信息</li>
	 *     <li>INFO：一般性操作失败信息</li>
	 * </ul>
	 * </p>
	 *
	 * @return 日志级别，默认为{@link Level#ERROR}
	 * @since 1.0.0
	 */
	Level level() default Level.ERROR;

	/**
	 * HTTP响应状态码
	 * <p>
	 * 指定抛出异常时返回的HTTP状态码。建议根据错误类型选择：
	 * <ul>
	 *     <li>400 BAD_REQUEST：参数验证失败、业务规则违反</li>
	 *     <li>401 UNAUTHORIZED：未登录或认证失败</li>
	 *     <li>403 FORBIDDEN：权限不足</li>
	 *     <li>404 NOT_FOUND：资源不存在</li>
	 *     <li>500 INTERNAL_SERVER_ERROR：服务器内部错误</li>
	 * </ul>
	 * </p>
	 *
	 * @return HTTP状态码，默认为{@link HttpStatus#OK}
	 * @since 1.0.0
	 */
	HttpStatus status() default HttpStatus.OK;
}