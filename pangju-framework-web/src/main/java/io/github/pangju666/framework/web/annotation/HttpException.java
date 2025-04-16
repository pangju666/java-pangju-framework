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
 * 该注解用于配置异常的HTTP响应行为，只能标注在{@link BaseHttpException}的子类上。
 * 通过此注解可以统一配置异常的错误码、类型、概述、日志记录行为和HTTP状态码。
 * </p>
 *
 * <p>
 * 错误码生成规则：
 * <ul>
 *     <li>最终错误码 = 异常类型基础码 + |注解配置码|（配置码的绝对值）</li>
 *     <li>异常类型基础码来自{@link HttpExceptionType}对应的基础错误码</li>
 *     <li>注解配置码通过{@link #code()}方法配置，使用其绝对值参与计算</li>
 *     <li>当配置码绝对值超过1000时，舍去千位及以上数值（如1234变为234）</li>
 *     <li>使用绝对值确保最终错误码始终为正数</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 假设SERVICE类型基础码为2000
 * @HttpException(
 *     code = 1234,        // 配置码为1234，舍去千位后变为234
 *     type = HttpExceptionType.SERVICE,  // 使用SERVICE类型
 *     description = "用户未找到异常"
 * )
 * public class UserNotFoundException extends BaseHttpException {
 *     // 最终错误码为：2234（2000 + 234）
 * }
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
public @interface HttpException {
	/**
	 * 异常配置码
	 * <p>
	 * 用于配置异常的具体错误码，此配置码的绝对值会与异常类型的基础码相加，
	 * 得到最终的错误码。例如：
	 * <ul>
	 *     <li>异常类型为SERVICE(基础码2000)，配置码为1，最终错误码为2001</li>
	 *     <li>异常类型为DATA_OPERATION(基础码3000)，配置码为1234，最终错误码为3234（3000+234）</li>
	 * </ul>
	 * 配置码可以为负数，但计算时将使用其绝对值。
	 * 当配置码绝对值超过1000时，会舍去千位及以上数值，只保留百位以下数值。
	 * </p>
	 *
	 * @return 异常配置码
	 * @since 1.0.0
	 */
	int code();

	/**
	 * 异常类型
	 * <p>
	 * 用于分类异常，默认为{@link HttpExceptionType#CUSTOM}。
	 * 可选值参见{@link HttpExceptionType}枚举。
	 * </p>
	 *
	 * @return 异常类型
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
	 * 控制是否记录该异常的日志信息，默认为true。
	 * 对于一些预期内的业务异常，可以设置为false以减少日志量。
	 * </p>
	 *
	 * @return 是否记录日志
	 * @since 1.0.0
	 */
	boolean log() default true;

	Level logLevel() default Level.ERROR;

	/**
	 * HTTP响应状态码
	 * <p>
	 * 指定抛出此异常时返回的HTTP状态码，默认为{@link HttpStatus#OK}。
	 * 建议根据实际的错误场景选择合适的HTTP状态码。
	 * </p>
	 *
	 * @return HTTP状态码
	 * @since 1.0.0
	 */
	HttpStatus status() default HttpStatus.OK;
}