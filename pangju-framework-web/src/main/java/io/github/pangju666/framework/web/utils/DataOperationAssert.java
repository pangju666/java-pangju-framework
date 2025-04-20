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

package io.github.pangju666.framework.web.utils;

import io.github.pangju666.framework.web.exception.data.*;
import io.github.pangju666.framework.web.model.error.DataOperationError;

/**
 * 数据操作断言工具类
 * <p>
 * 提供一组用于数据操作断言的实用方法，用于简化数据操作异常的处理：
 * <ul>
 *     <li>查询断言：判断查询相关条件，失败时抛出 {@link DataQueryException}</li>
 *     <li>创建断言：判断创建相关条件，失败时抛出 {@link DataCreateException}</li>
 *     <li>更新断言：判断更新相关条件，失败时抛出 {@link DataUpdateException}</li>
 *     <li>保存断言：判断保存相关条件，失败时抛出 {@link DataSaveException}</li>
 *     <li>删除断言：判断删除相关条件，失败时抛出 {@link DataRemoveException}</li>
 * </ul>
 * </p>
 *
 * <p>
 * 核心特性：
 * <ul>
 *     <li>简化异常处理：封装条件判断和异常抛出逻辑，提高代码可读性</li>
 *     <li>语义化方法：根据操作类型提供对应的断言方法，使代码意图更明确</li>
 *     <li>统一错误处理：确保在相同类型的数据操作中使用一致的异常处理策略</li>
 *     <li>支持多种异常构建方式：包括简单消息、自定义消息和结构化错误信息</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景：
 * <pre>{@code
 * // 查询断言示例
 * DataOperationAssert.query(user != null, "用户不存在");
 *
 * // 创建断言示例（带结构化错误信息）
 * DataOperationError error = new DataOperationError(
 *     "用户表",       // 数据来源
 *     "用户名",      // 数据描述
 *     username,    // 数据值
 *     "用户名已被占用" // 错误原因
 * );
 * DataOperationAssert.create(!userExists, "创建用户失败", error);
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @see io.github.pangju666.framework.web.exception.data.DataQueryException 数据查询异常
 * @see io.github.pangju666.framework.web.exception.data.DataCreateException 数据创建异常
 * @see io.github.pangju666.framework.web.exception.data.DataUpdateException 数据更新异常
 * @see io.github.pangju666.framework.web.exception.data.DataSaveException 数据保存异常
 * @see io.github.pangju666.framework.web.exception.data.DataRemoveException 数据删除异常
 * @see io.github.pangju666.framework.web.model.error.DataOperationError 数据操作错误记录
 * @since 1.0.0
 */
public class DataOperationAssert {
	protected DataOperationAssert() {
	}

	/**
	 * 查询操作断言（基本版）
	 * <p>
	 * 断言表达式为真，否则抛出包含指定错误原因的数据查询异常。
	 * </p>
	 *
	 * @param expression 断言表达式，为 {@code false} 时抛出异常
	 * @param reason     错误原因，描述查询失败的具体原因
	 * @throws DataQueryException 当断言表达式为 {@code false} 时抛出
	 * @since 1.0.0
	 */
	public static void query(final boolean expression, final String reason) {
		if (!expression) {
			throw new DataQueryException(reason);
		}
	}

	/**
	 * 查询操作断言（自定义消息版）
	 * <p>
	 * 断言表达式为真，否则抛出包含自定义错误消息和错误原因的数据查询异常。
	 * </p>
	 *
	 * @param expression 断言表达式，为 {@code false} 时抛出异常
	 * @param message    自定义错误消息，替代默认的"数据查询错误"
	 * @param reason     错误原因，描述查询失败的具体原因
	 * @throws DataQueryException 当断言表达式为 {@code false} 时抛出
	 * @since 1.0.0
	 */
	public static void query(final boolean expression, final String message, final String reason) {
		if (!expression) {
			throw new DataQueryException(message, reason);
		}
	}

	/**
	 * 查询操作断言（结构化错误信息版）
	 * <p>
	 * 断言表达式为真，否则抛出包含自定义错误消息和结构化错误信息的数据查询异常。
	 * </p>
	 *
	 * @param expression 断言表达式，为 {@code false} 时抛出异常
	 * @param message    自定义错误消息，替代默认的"数据查询错误"
	 * @param error      数据操作错误信息对象，包含来源、描述、数据值和错误原因
	 * @throws DataQueryException 当断言表达式为 {@code false} 时抛出
	 * @since 1.0.0
	 */
	public static void query(final boolean expression, final String message, final DataOperationError error) {
		if (!expression) {
			throw new DataQueryException(message, error);
		}
	}

	/**
	 * 创建操作断言（基本版）
	 * <p>
	 * 断言表达式为真，否则抛出包含指定错误原因的数据创建异常。
	 * </p>
	 *
	 * @param expression 断言表达式，为 {@code false} 时抛出异常
	 * @param reason 错误原因，描述创建失败的具体原因
	 * @throws DataCreateException 当断言表达式为 {@code false} 时抛出
	 * @since 1.0.0
	 */
	public static void create(final boolean expression, final String reason) {
		if (!expression) {
			throw new DataCreateException(reason);
		}
	}

	/**
	 * 创建操作断言（自定义消息版）
	 * <p>
	 * 断言表达式为真，否则抛出包含自定义错误消息和错误原因的数据创建异常。
	 * </p>
	 *
	 * @param expression 断言表达式，为 {@code false} 时抛出异常
	 * @param message 自定义错误消息，替代默认的"数据创建错误"
	 * @param reason 错误原因，描述创建失败的具体原因
	 * @throws DataCreateException 当断言表达式为 {@code false} 时抛出
	 * @since 1.0.0
	 */
	public static void create(final boolean expression, final String message, final String reason) {
		if (!expression) {
			throw new DataCreateException(message, reason);
		}
	}

	/**
	 * 创建操作断言（结构化错误信息版）
	 * <p>
	 * 断言表达式为真，否则抛出包含自定义错误消息和结构化错误信息的数据创建异常。
	 * </p>
	 *
	 * @param expression 断言表达式，为 {@code false} 时抛出异常
	 * @param message 自定义错误消息，替代默认的"数据创建错误"
	 * @param error 数据操作错误信息对象，包含来源、描述、数据值和错误原因
	 * @throws DataCreateException 当断言表达式为 {@code false} 时抛出
	 * @since 1.0.0
	 */
	public static void create(final boolean expression, final String message, final DataOperationError error) {
		if (!expression) {
			throw new DataCreateException(message, error);
		}
	}

	/**
	 * 更新操作断言（基本版）
	 * <p>
	 * 断言表达式为真，否则抛出包含指定错误原因的数据更新异常。
	 * </p>
	 *
	 * @param expression 断言表达式，为 {@code false} 时抛出异常
	 * @param reason 错误原因，描述更新失败的具体原因
	 * @throws DataUpdateException 当断言表达式为 {@code false} 时抛出
	 * @since 1.0.0
	 */
	public static void update(final boolean expression, final String reason) {
		if (!expression) {
			throw new DataUpdateException(reason);
		}
	}

	/**
	 * 更新操作断言（自定义消息版）
	 * <p>
	 * 断言表达式为真，否则抛出包含自定义错误消息和错误原因的数据更新异常。
	 * </p>
	 *
	 * @param expression 断言表达式，为 {@code false} 时抛出异常
	 * @param message 自定义错误消息，替代默认的"数据更新错误"
	 * @param reason 错误原因，描述更新失败的具体原因
	 * @throws DataUpdateException 当断言表达式为 {@code false} 时抛出
	 * @since 1.0.0
	 */
	public static void update(final boolean expression, final String message, final String reason) {
		if (!expression) {
			throw new DataUpdateException(message, reason);
		}
	}

	/**
	 * 更新操作断言（结构化错误信息版）
	 * <p>
	 * 断言表达式为真，否则抛出包含自定义错误消息和结构化错误信息的数据更新异常。
	 * </p>
	 *
	 * @param expression 断言表达式，为 {@code false} 时抛出异常
	 * @param message 自定义错误消息，替代默认的"数据更新错误"
	 * @param error 数据操作错误信息对象，包含来源、描述、数据值和错误原因
	 * @throws DataUpdateException 当断言表达式为 {@code false} 时抛出
	 * @since 1.0.0
	 */
	public static void update(final boolean expression, final String message, final DataOperationError error) {
		if (!expression) {
			throw new DataUpdateException(message, error);
		}
	}

	/**
	 * 保存操作断言（基本版）
	 * <p>
	 * 断言表达式为真，否则抛出包含指定错误原因的数据保存异常。
	 * </p>
	 *
	 * @param expression 断言表达式，为 {@code false} 时抛出异常
	 * @param reason 错误原因，描述保存失败的具体原因
	 * @throws DataSaveException 当断言表达式为 {@code false} 时抛出
	 * @since 1.0.0
	 */
	public static void save(final boolean expression, final String reason) {
		if (!expression) {
			throw new DataSaveException(reason);
		}
	}

	/**
	 * 保存操作断言（自定义消息版）
	 * <p>
	 * 断言表达式为真，否则抛出包含自定义错误消息和错误原因的数据保存异常。
	 * </p>
	 *
	 * @param expression 断言表达式，为 {@code false} 时抛出异常
	 * @param message 自定义错误消息，替代默认的"数据保存错误"
	 * @param reason 错误原因，描述保存失败的具体原因
	 * @throws DataSaveException 当断言表达式为 {@code false} 时抛出
	 * @since 1.0.0
	 */
	public static void save(final boolean expression, final String message, final String reason) {
		if (!expression) {
			throw new DataSaveException(message, reason);
		}
	}

	/**
	 * 保存操作断言（结构化错误信息版）
	 * <p>
	 * 断言表达式为真，否则抛出包含自定义错误消息和结构化错误信息的数据保存异常。
	 * </p>
	 *
	 * @param expression 断言表达式，为 {@code false} 时抛出异常
	 * @param message    自定义错误消息，替代默认的"数据保存错误"
	 * @param error      数据操作错误信息对象，包含来源、描述、数据值和错误原因
	 * @throws DataSaveException 当断言表达式为 {@code false} 时抛出
	 * @since 1.0.0
	 */
	public static void save(final boolean expression, final String message, final DataOperationError error) {
		if (!expression) {
			throw new DataSaveException(message, error);
		}
	}

	/**
	 * 删除操作断言（基本版）
	 * <p>
	 * 断言表达式为真，否则抛出包含指定错误原因的数据删除异常。
	 * </p>
	 *
	 * @param expression 断言表达式，为 {@code false} 时抛出异常
	 * @param reason     错误原因，描述删除失败的具体原因
	 * @throws DataRemoveException 当断言表达式为 {@code false} 时抛出
	 * @since 1.0.0
	 */
	public static void remove(final boolean expression, final String reason) {
		if (!expression) {
			throw new DataRemoveException(reason);
		}
	}

	/**
	 * 删除操作断言（自定义消息版）
	 * <p>
	 * 断言表达式为真，否则抛出包含自定义错误消息和错误原因的数据删除异常。
	 * </p>
	 *
	 * @param expression 断言表达式，为 {@code false} 时抛出异常
	 * @param message    自定义错误消息，替代默认的"数据删除错误"
	 * @param reason     错误原因，描述删除失败的具体原因
	 * @throws DataRemoveException 当断言表达式为 {@code false} 时抛出
	 * @since 1.0.0
	 */
	public static void remove(final boolean expression, final String message, final String reason) {
		if (!expression) {
			throw new DataRemoveException(message, reason);
		}
	}

	/**
	 * 删除操作断言（结构化错误信息版）
	 * <p>
	 * 断言表达式为真，否则抛出包含自定义错误消息和结构化错误信息的数据删除异常。
	 * </p>
	 *
	 * @param expression 断言表达式，为 {@code false} 时抛出异常
	 * @param message    自定义错误消息，替代默认的"数据删除错误"
	 * @param error      数据操作错误信息对象，包含来源、描述、数据值和错误原因
	 * @throws DataRemoveException 当断言表达式为 {@code false} 时抛出
	 * @since 1.0.0
	 */
	public static void remove(final boolean expression, final String message, final DataOperationError error) {
		if (!expression) {
			throw new DataRemoveException(message, error);
		}
	}
}
