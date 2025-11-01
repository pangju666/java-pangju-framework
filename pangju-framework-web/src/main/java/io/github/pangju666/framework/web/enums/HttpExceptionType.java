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

package io.github.pangju666.framework.web.enums;

import io.github.pangju666.framework.web.annotation.HttpException;
import io.github.pangju666.framework.web.pool.WebConstants;

/**
 * HTTP异常类型枚举
 * <p>
 * 定义系统中所有HTTP异常的分类类型，每种类型包含基础错误码和描述信息。
 * 配合{@link HttpException}注解使用，用于生成统一的异常错误码。
 * </p>
 *
 * <p>
 * 异常类型及基础码：
 * <ul>
 *     <li>SERVICE(1000): 业务逻辑错误</li>
 *     <li>DATA_OPERATION(2000): 数据操作错误</li>
 *     <li>AUTHENTICATION(3000): 认证授权错误</li>
 *     <li>VALIDATION(4000): 参数校验错误</li>
 *     <li>SERVER(5000): 服务器内部错误</li>
 *     <li>CUSTOM(6000): 自定义错误</li>
 *     <li>UNKNOWN(0): 未知错误（框架内部使用）</li>
 * </ul>
 * </p>
 *
 * <p>
 * 错误码计算规则：
 * <ul>
 *     <li>最终错误码 = -(基础码 + |配置码|)</li>
 *     <li>配置码大于1000时，仅保留后三位（如1234变为234）</li>
 *     <li>使用负数表示错误状态</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 定义异常类
 * @HttpException(
 *     code = 234,
 *     type = HttpExceptionType.SERVICE,
 *     description = "用户余额不足"
 * )
 * public class InsufficientBalanceException extends ServiceException {
 *     // 最终错误码：-1234（-1000 + 234）
 * }
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @see HttpException
 * @since 1.0.0
 */
public enum HttpExceptionType {
	/**
	 * 服务器内部错误（基础码：5000）
	 * <p>
	 * 用于表示系统运行时发生的内部错误，包括：
	 * <ul>
	 *     <li>系统运行时异常：如空指针、类型转换、数组越界等</li>
	 *     <li>资源访问异常：如文件读写、网络连接、数据库访问等</li>
	 *     <li>系统配置错误：如配置文件缺失、配置项无效等</li>
	 *     <li>第三方服务调用异常：如RPC调用超时、服务不可用等</li>
	 * </ul>
	 * </p>
	 *
	 * @since 1.0.0
	 */
	SERVER(5000, "服务器内部错误"),
	/**
	 * 业务逻辑错误（基础码：1000）
	 * <p>
	 * 用于表示业务处理过程中的错误，包括：
	 * <ul>
	 *     <li>业务规则校验失败：如余额不足、库存不足等</li>
	 *     <li>业务流程处理失败：如订单状态流转异常、支付流程中断等</li>
	 *     <li>业务状态异常：如重复提交、数据已过期等</li>
	 *     <li>业务依赖服务异常：如短信发送失败、邮件推送失败等</li>
	 * </ul>
	 * </p>
	 *
	 * @since 1.0.0
	 */
	SERVICE(1000, "业务逻辑错误"),
	/**
	 * 数据操作错误（基础码：2000）
	 * <p>
	 * 用于表示数据操作相关的错误，包括：
	 * <ul>
	 *     <li>数据库操作异常：如SQL执行错误、事务处理失败等</li>
	 *     <li>数据一致性错误：如主键冲突、外键约束等</li>
	 *     <li>数据格式错误：如JSON解析失败、日期格式错误等</li>
	 *     <li>数据完整性错误：如必要数据缺失、数据损坏等</li>
	 * </ul>
	 * </p>
	 *
	 * @since 1.0.0
	 */
	DATA_OPERATION(2000, "数据操作错误"),
	/**
	 * 认证错误（基础码：3000）
	 * <p>
	 * 用于表示用户认证和授权相关的错误，包括：
	 * <ul>
	 *     <li>用户认证失败：如用户名密码错误、账号被锁定等</li>
	 *     <li>用户未登录：如会话过期、Token失效等</li>
	 *     <li>权限不足：如越权访问、角色权限不足等</li>
	 *     <li>安全校验失败：如签名验证失败、时间戳过期等</li>
	 * </ul>
	 * </p>
	 *
	 * @since 1.0.0
	 */
	AUTHENTICATION(3000, "认证错误"),
	/**
	 * 参数校验错误（基础码：4000）
	 * <p>
	 * 用于表示请求参数验证相关的错误，包括：
	 * <ul>
	 *     <li>参数格式错误：如数字格式、日期格式、邮箱格式等</li>
	 *     <li>必填项缺失：如必要参数为空、ID未传等</li>
	 *     <li>参数值范围错误：如数值越界、字符串长度超限等</li>
	 *     <li>参数组合错误：如互斥参数、依赖参数等</li>
	 * </ul>
	 * </p>
	 *
	 * @since 1.0.0
	 */
	VALIDATION(4000, "参数校验错误"),
	/**
	 * 自定义错误（基础码：6000）
	 * <p>
	 * 用于处理特定业务场景的自定义异常，适用于：
	 * <ul>
	 *     <li>特定业务场景：如特殊的业务规则验证</li>
	 *     <li>自定义处理流程：如特殊的错误处理逻辑</li>
	 *     <li>临时性异常：如系统维护、功能降级等</li>
	 * </ul>
	 * 建议仅在确实无法归类到其他异常类型时使用此类型。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	CUSTOM(6000, "自定义错误"),
	/**
	 * 未知错误（固定错误码：{@link WebConstants#BASE_ERROR_CODE}）
	 *
	 * <p>
	 * 框架内部使用的异常类型，用于处理未标注{@link HttpException}注解的异常类。
	 * </p>
	 *
	 * <p>
	 * 作为默认的异常类型，具有以下特点：
	 * <ul>
	 *     <li>错误码固定为{@link WebConstants#BASE_ERROR_CODE}，不参与基础码机制</li>
	 *     <li>仅在异常类未标注{@link HttpException}注解时使用</li>
	 *     <li>不建议在业务代码中显式使用此类型</li>
	 * </ul>
	 * </p>
	 *
	 * @since 1.0.0
	 */
	UNKNOWN(0, "未知错误");

	/**
	 * 异常类型基础码
	 * <p>
	 * 用于生成最终错误码的基础值，与{@link HttpException#code()}组合计算：
	 * </p>
	 * <p>
	 * 计算公式：-(基础码 + |配置码|) <br>
	 * 示例：SERVICE(1000) + code(234) = -1234
	 * </p>
	 *
	 * @since 1.0.0
	 */
	private final int baseCode;
	/**
	 * 异常类型描述
	 * <p>
	 * 用于描述该异常类型的具体含义，便于理解和使用。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	private final String label;

	/**
	 * 构造HTTP异常类型
	 *
	 * @param baseCode 异常类型基础码，用于生成最终错误码，如SERVICE类型使用1000
	 * @param label    异常类型的描述标签，用于描述该类型的基本含义
	 * @since 1.0.0
	 */
	HttpExceptionType(int baseCode, String label) {
		this.baseCode = baseCode;
		this.label = label;
	}

	/**
	 * 获取异常类型的描述信息
	 *
	 * @return 异常类型的描述标签，如"业务逻辑错误"、"参数校验错误"等
	 * @since 1.0.0
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * 获取异常类型的基础错误码
	 *
	 * @return 异常类型的基础错误码，如SERVICE返回1000，DATA返回2000等
	 * @since 1.0.0
	 */
	public int getBaseCode() {
		return baseCode;
	}

	/**
	 * 计算最终错误码
	 * <p>
	 * 根据异常类型和配置码计算最终的错误码：
	 * <ul>
	 *     <li>{@link #UNKNOWN}类型：直接返回{@link WebConstants#BASE_ERROR_CODE}</li>
	 *     <li>其他类型：-(基础码 + (|配置码| > 1000 ? |配置码| % 1000 : |配置码|))</li>
	 * </ul>
	 * </p>
	 *
	 * <p>
	 * 计算示例：
	 * <ul>
	 *     <li>UNKNOWN: 返回{@link WebConstants#BASE_ERROR_CODE}</li>
	 *     <li>SERVICE(1000) + code(234) = -1234</li>
	 *     <li>DATA_OPERATION(2000) + code(1234) = -2234（保留后三位234）</li>
	 *     <li>VALIDATION(4000) + code(-56) = -4056（取绝对值56）</li>
	 * </ul>
	 * </p>
	 *
	 * @param code 配置的错误码，通常来自{@link HttpException#code()}
	 * @return 计算后的最终错误码（负数）
	 * @since 1.0.0
	 */
	public int computeCode(int code) {
		if (this == UNKNOWN) {
			return WebConstants.BASE_ERROR_CODE;
		}
		int absCode = Math.abs(code);
		return -(baseCode + (absCode > 1000 ? absCode % 1000 : absCode));
	}
}