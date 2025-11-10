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

package io.github.pangju666.framework.data.mongodb.utils;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * MongoDB 查询构造工具
 * <p>
 * 以静态方法形式封装常见查询构造场景，便于快速、规范地生成 {@link org.springframework.data.mongodb.core.query.Query} 与 {@link org.springframework.data.mongodb.core.query.Criteria}。
 * </p>
 *
 * <p>支持的场景：</p>
 * <ul>
 *   <li>字段值为空/非空（{@link #queryByKeyNull(String)}、{@link #queryByKeyNotNull(String)}）</li>
 *   <li>字段等值/不等值（{@link #queryByKeyValue(String, Object)}、{@link #queryByKeyNotValue(String, Object)}）</li>
 *   <li>值集合包含/排除（{@link #queryByKeyValues(String, Collection)}、{@link #queryByKeyNotValues(String, Collection)}）</li>
 *   <li>正则匹配/不匹配（支持 {@link String} 与 {@link Pattern}：{@link #queryByKeyRegex(String, String)}、{@link #queryByKeyRegex(String, Pattern)}、{@link #queryByKeyNotRegex(String, String)}、{@link #queryByKeyNotRegex(String, Pattern)}）</li>
 * </ul>
 *
 * <p>空值语义说明：</p>
 * <ul>
 *   <li>“为空”通过 {@code $or} 组合两类条件：字段值为 {@code null}，或字段不存在（{@link #nullValueCriteria(String)}）</li>
 *   <li>“非空”通过 {@code $and} 组合两类条件：字段值不为 {@code null}，且字段存在（{@link #notNullValueCriteria(String)}）</li>
 * </ul>
 *
 * <p>验证约定：</p>
 * <ul>
 *   <li>所有接收 {@code key} 的方法在构造条件前都会校验非空（直接或通过内部条件方法）并在不满足时抛出 {@link IllegalArgumentException}</li>
 * </ul>
 *
 * @author pangju666
 * @since 1.0.0
 * @see Query
 * @see Criteria
 */
public class QueryUtils {
    protected QueryUtils() {
    }

    /**
	 * 构建“字段值为空”的查询。
     * <p>
	 * 使用 {@code $or} 操作符组合两类条件：
	 * </p>
     * <ul>
	 *   <li>字段值为 {@code null}</li>
	 *   <li>字段不存在</li>
     * </ul>
     *
	 * @param key 字段名，不可为空或空白
     * @return 查询对象
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白时抛出
     * @since 1.0.0
     */
	public static Query queryByKeyNull(final String key) {
		Assert.hasText(key, "key 不可为空");

		return Query.query(nullValueCriteria(key));
    }

    /**
	 * 构建“字段值非空”的查询。
     * <p>
	 * 使用 {@code $and} 操作符组合两类条件：
	 * </p>
     * <ul>
	 *   <li>字段值不为 {@code null}</li>
	 *   <li>字段存在</li>
     * </ul>
     *
	 * @param key 字段名，不可为空或空白
     * @return 查询对象
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白时抛出
     * @since 1.0.0
     */
	public static Query queryByKeyNotNull(final String key) {
		Assert.hasText(key, "key 不可为空");

		return Query.query(notNullValueCriteria(key));
    }

    /**
	 * 构建字段等值查询
     * <ul>
	 *   <li>当 {@code value} 为 {@code null}：返回“字段值为 {@code null} 或字段不存在”的查询</li>
	 *   <li>当 {@code value} 非 {@code null}：返回“字段值等于 {@code value}”的查询</li>
     * </ul>
     *
	 * @param key   字段名，不能为空或空白
	 * @param value 字段值，可为 {@code null}
     * @return 查询对象
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白时抛出
     * @since 1.0.0
     */
	public static Query queryByKeyValue(final String key, @Nullable final Object value) {
		Assert.hasText(key, "key 不可为空");

		if (Objects.isNull(value)) {
			return Query.query(nullValueCriteria(key));
        }
        return Query.query(Criteria.where(key).is(value));
    }

    /**
	 * 构建字段不等值查询
     * <ul>
	 *   <li>当 {@code value} 为 {@code null}：返回“字段值不为 {@code null} 且字段存在”的查询</li>
	 *   <li>当 {@code value} 非 {@code null}：返回“字段值不等于 {@code value}”的查询</li>
     * </ul>
     *
	 * @param key   字段名，不能为空或空白
	 * @param value 要排除的字段值，可为 {@code null}
     * @return 查询对象
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白时抛出
     * @since 1.0.0
     */
	public static Query queryByKeyNotValue(final String key, @Nullable final Object value) {
		Assert.hasText(key, "key 不可为空");

		if (Objects.isNull(value)) {
			return Query.query(notNullValueCriteria(key));
        }
        return Query.query(Criteria.where(key).ne(value));
    }

    /**
	 * 构建集合包含查询（$in）
     * <p>
	 * 使用 {@code $in} 匹配字段值属于指定集合的文档。
     * </p>
     *
	 * @param key    字段名，不能为空或空白
	 * @param values 值集合，不可为 {@code null} 且至少包含一个元素
     * @return 查询对象
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白，或 {@code values} 为空时抛出
     * @since 1.0.0
     */
	public static Query queryByKeyValues(final String key, final Collection<?> values) {
        Assert.hasText(key, "key 不可为空");
        Assert.notEmpty(values, "values 不可为空");

        return Query.query(Criteria.where(key).in(values));
    }

    /**
	 * 构建集合排除查询（$nin）
     * <p>
	 * 使用 {@code $nin} 匹配字段值不属于指定集合的文档。
     * </p>
     *
	 * @param key    字段名，不能为空或空白
	 * @param values 值集合，不可为 {@code null} 且至少包含一个元素
     * @return 查询对象
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白，或 {@code values} 为空时抛出
     * @since 1.0.0
     */
	public static Query queryByKeyNotValues(final String key, final Collection<?> values) {
        Assert.hasText(key, "key 不可为空");
        Assert.notEmpty(values, "values 不可为空");

        return Query.query(Criteria.where(key).nin(values));
    }

    /**
	 * 构建正则匹配查询（字符串）
     * <p>
	 * 使用 MongoDB 正则查询匹配字符串值。大小写敏感性与匹配选项取决于服务器或索引配置。
	 * 若需指定匹配选项（如大小写、单行/多行），请使用 {@link #queryByKeyRegex(String, Pattern)}。
	 * 性能提示：正则前缀或锚点（例如 {@code ^prefix}）通常更易于利用索引。
     * </p>
     *
	 * @param key   字段名，不能为空或空白
	 * @param regex 正则表达式字符串，不能为空或空白
     * @return 查询对象
	 * @throws IllegalArgumentException 当 {@code key} 或 {@code regex} 为空或空白时抛出
     * @since 1.0.0
     */
	public static Query queryByKeyRegex(final String key, final String regex) {
        Assert.hasText(key, "key 不可为空");
        Assert.hasText(regex, "regex 不可为空");

        return Query.query(Criteria.where(key).regex(regex));
    }

    /**
	 * 构建正则匹配查询（Pattern）
     * <p>
	 * 使用 MongoDB 正则查询并通过 {@link Pattern} 指定更丰富的匹配选项（大小写、单行/多行等）。
	 * 性能提示：优先使用可索引的前缀或锚点模式以避免全表扫描。
     * </p>
     *
	 * @param key     字段名，不能为空或空白
	 * @param pattern Java 正则表达式 {@link Pattern}，不可为 {@code null}
     * @return 查询对象
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白，或 {@code pattern} 为 {@code null} 时抛出
     * @since 1.0.0
     */
	public static Query queryByKeyRegex(final String key, final Pattern pattern) {
        Assert.hasText(key, "key 不可为空");
        Assert.notNull(pattern, "pattern 不可为null");

        return Query.query(Criteria.where(key).regex(pattern));
    }

    /**
	 * 构建正则不匹配查询（字符串）
     * <p>
	 * 使用 MongoDB 正则“不匹配”查询；匹配选项说明参见 {@link #queryByKeyRegex(String, String)}。
     * </p>
     *
	 * @param key   字段名，不能为空或空白
	 * @param regex 正则表达式字符串，不能为空或空白
     * @return 查询对象
	 * @throws IllegalArgumentException 当 {@code key} 或 {@code regex} 为空或空白时抛出
     * @since 1.0.0
     */
	public static Query queryByKeyNotRegex(final String key, final String regex) {
        Assert.hasText(key, "key 不可为空");
        Assert.hasText(regex, "regex 不可为空");

        return Query.query(Criteria.where(key).not().regex(regex));
    }

    /**
	 * 构建正则不匹配查询（Pattern）
     * <p>
	 * 使用 MongoDB 正则“不匹配”查询；可通过 {@link Pattern} 指定更丰富的匹配选项。
     * </p>
     *
	 * @param key     字段名，不能为空或空白
	 * @param pattern Java 正则表达式 {@link Pattern}，不可为 {@code null}
     * @return 查询对象
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白，或 {@code pattern} 为 {@code null} 时抛出
     * @since 1.0.0
     */
	public static Query queryByKeyNotRegex(final String key, final Pattern pattern) {
        Assert.hasText(key, "key 不可为空");
        Assert.notNull(pattern, "pattern 不可为null");

        return Query.query(Criteria.where(key).not().regex(pattern));
    }

    /**
	 * 构建“字段值为空”的条件
     * <p>
	 * 使用 {@code $or} 组合两类条件：字段值为 {@code null} 或字段不存在。
     * </p>
     *
	 * @param key 字段名，不能为空或空白
     * @return 条件对象
     * @since 1.0.0
     */
	protected static Criteria nullValueCriteria(final String key) {
        Criteria nullValueCriteria = Criteria.where(key).isNullValue();
        Criteria nullCriteria = Criteria.where(key).isNull();
        return new Criteria().orOperator(nullValueCriteria, nullCriteria);
    }

    /**
	 * 构建“字段值非空”的条件
     * <p>
	 * 使用 {@code $and} 组合两类条件：字段值不为 {@code null} 且字段存在。
     * </p>
     *
	 * @param key 字段名，不能为空或空白
     * @return 条件对象
     * @since 1.0.0
     */
	protected static Criteria notNullValueCriteria(final String key) {
        Criteria notNullValueValueCriteria = Criteria.where(key).not().isNullValue();
        Criteria notNullCriteria = Criteria.where(key).ne(null);
        return new Criteria().andOperator(notNullValueValueCriteria, notNullCriteria);
    }
}
