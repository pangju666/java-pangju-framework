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

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import io.github.pangju666.framework.data.mongodb.lang.MongoConstants;
import org.bson.Document;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Collection;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * MongoDB 查询构造工具。
 *
 * <p>
 * 提供便捷的静态方法以构建 {@link Query}，覆盖以下常见场景：
 * </p>
 * <ul>
 *   <li>空查询占位（{@link #emptyQuery()} 与 {@link #EMPTY_QUERY}）</li>
 *   <li>字段值为空/非空（{@link #queryByNullValue(String)}、{@link #queryByNotNullValue(String)}）</li>
 *   <li>ID 等值、包含（{@code $in}）、排除（{@code $nin}）（{@link #queryById(String)}、{@link #queryByIds(Collection)}、{@link #queryByNotIds(Collection)}）</li>
 *   <li>字段等值/不等值（{@link #queryByValue(String, Object)}、{@link #queryByNotValue(String, Object)}）</li>
 *   <li>值集合包含/排除（{@link #queryByValues(String, Collection)}、{@link #queryByNotValues(String, Collection)}）</li>
 *   <li>正则匹配/不匹配（支持 {@link String} 与 {@link Pattern}，{@link #queryByRegex(String, String)}、{@link #queryByRegex(String, Pattern)}、{@link #queryByNotRegex(String, String)}、{@link #queryByNotRegex(String, Pattern)}）</li>
 * </ul>
 *
 * <p>
 * 空值相关的构造说明：
 * </p>
 * <ul>
 *   <li>“为空”使用 {@code $or} 组合：值为 {@code null} 或字段不存在（{@link #nullValueCriteria(String)}）。</li>
 *   <li>“非空”使用 {@code $and} 组合：值不为 {@code null} 且字段存在（{@link #notNullValueCriteria(String)}）。</li>
 * </ul>
 *
 * @author pangju666
 * @since 1.0.0
 * @see Query
 * @see Criteria
 */
public class QueryUtils {
    /**
	 * 空查询对象常量。
     * <p>
	 * 一个不可变的 {@link Query} 实例，其所有操作方法均返回自身且不改变查询条件。
	 * 适用于需要占位但无需附带任何查询条件的场景。
     * </p>
     *
     * @since 1.0.0
     */
    public static final Query EMPTY_QUERY = new EmptyQuery();

	/**
	 * 工具类构造器。
	 * <p>受保护以避免实例化。</p>
	 */
    protected QueryUtils() {
    }

    /**
	 * 获取空查询对象。
     *
	 * @return 一个不可变的空查询对象，不携带任何条件
     * @since 1.0.0
     */
    public static Query emptyQuery() {
        return EMPTY_QUERY;
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
	public static Query queryByNullValue(final String key) {
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
	public static Query queryByNotNullValue(final String key) {
		return Query.query(notNullValueCriteria(key));
    }

    /**
	 * 根据 ID 构建等值查询。
     *
	 * @param id 文档 ID，不可为空或空白
     * @return 查询对象
	 * @throws IllegalArgumentException 当 {@code id} 为空或空白时抛出
     * @since 1.0.0
     */
	public static Query queryById(final String id) {
		Assert.hasText(id, "id 不可为空");

        return Query.query(Criteria.where(MongoConstants.ID_FIELD_NAME).is(id));
    }

    /**
	 * 根据 ID 集合构建包含查询。
     * <p>
	 * 使用 {@code $in} 操作符查询 ID 属于指定集合的文档。
     * </p>
     *
	 * @param ids 文档 ID 集合，不可为 {@code null} 且至少包含一个元素
     * @return 查询对象
	 * @throws IllegalArgumentException 当 {@code ids} 为空时抛出
     * @since 1.0.0
     */
	public static Query queryByIds(final Collection<String> ids) {
		Assert.notEmpty(ids, "ids 不可为空");

        return Query.query(Criteria.where(MongoConstants.ID_FIELD_NAME).in(ids));
    }

    /**
	 * 根据 ID 集合构建排除查询。
     * <p>
	 * 使用 {@code $nin} 操作符查询 ID 不属于指定集合的文档。
     * </p>
     *
	 * @param ids 文档 ID 集合，不可为 {@code null} 且至少包含一个元素
     * @return 查询对象
	 * @throws IllegalArgumentException 当 {@code ids} 为空时抛出
     * @since 1.0.0
     */
	public static Query queryByNotIds(final Collection<String> ids) {
        Assert.notEmpty(ids, "ids 不可为空");

        return Query.query(Criteria.where(MongoConstants.ID_FIELD_NAME).nin(ids));
    }

    /**
	 * 根据字段名和值构建等值查询。
     * <ul>
	 *   <li>当 {@code value} 为 {@code null} 时：查询“字段值为 {@code null} 或字段不存在”的文档。</li>
	 *   <li>当 {@code value} 非 {@code null} 时：查询“字段值等于 {@code value}”的文档。</li>
     * </ul>
     *
	 * @param key   要查询的字段名，不可为空或空白
	 * @param value 要查询的字段值，可为 {@code null}
     * @return 查询对象
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白时抛出
     * @since 1.0.0
     */
	public static Query queryByValue(final String key, final Object value) {
		if (Objects.isNull(value)) {
			return Query.query(nullValueCriteria(key));
        }

		Assert.hasText(key, "key 不可为空");
        return Query.query(Criteria.where(key).is(value));
    }

    /**
	 * 根据字段名和值构建不等值查询。
     * <ul>
	 *   <li>当 {@code value} 为 {@code null} 时：查询“字段值不为 {@code null} 且字段存在”的文档。</li>
	 *   <li>当 {@code value} 非 {@code null} 时：查询“字段值不等于 {@code value}”的文档。</li>
     * </ul>
     *
	 * @param key   要查询的字段名，不可为空或空白
	 * @param value 要排除的字段值，可为 {@code null}
     * @return 查询对象
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白时抛出
     * @since 1.0.0
     */
	public static Query queryByNotValue(final String key, final Object value) {
        if (Objects.isNull(value)) {
			return Query.query(notNullValueCriteria(key));
        }

        Assert.hasText(key, "key 不可为空");
        return Query.query(Criteria.where(key).ne(value));
    }

    /**
	 * 根据字段名和值集合构建包含查询。
     * <p>
	 * 使用 {@code $in} 操作符查询字段值属于指定集合的文档。
     * </p>
     *
	 * @param key    要查询的字段名，不可为空或空白
	 * @param values 要匹配的值集合，不可为 {@code null} 且至少包含一个元素
     * @return 查询对象
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白，或 {@code values} 为空时抛出
     * @since 1.0.0
     */
	public static Query queryByValues(final String key, final Collection<?> values) {
        Assert.hasText(key, "key 不可为空");
        Assert.notEmpty(values, "values 不可为空");

        return Query.query(Criteria.where(key).in(values));
    }

    /**
	 * 根据字段名和值集合构建排除查询。
     * <p>
	 * 使用 {@code $nin} 操作符查询字段值不属于指定集合的文档。
     * </p>
     *
	 * @param key    要查询的字段名，不可为空或空白
	 * @param values 要排除的值集合，不可为 {@code null} 且至少包含一个元素
     * @return 查询对象
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白，或 {@code values} 为空时抛出
     * @since 1.0.0
     */
	public static Query queryByNotValues(final String key, final Collection<?> values) {
        Assert.hasText(key, "key 不可为空");
        Assert.notEmpty(values, "values 不可为空");

        return Query.query(Criteria.where(key).nin(values));
    }

    /**
	 * 根据字段名和正则表达式字符串构建匹配查询。
     * <p>
	 * 使用 MongoDB 的正则表达式进行模式匹配查询，大小写敏感性与匹配选项由服务器端默认或索引配置决定。
	 * 若需指定匹配选项，请使用 {@link #queryByRegex(String, Pattern)}。
     * </p>
     *
	 * @param key   要查询的字段名，不可为空或空白
	 * @param regex 正则表达式字符串，不可为空或空白
     * @return 查询对象
	 * @throws IllegalArgumentException 当 {@code key} 或 {@code regex} 为空或空白时抛出
     * @since 1.0.0
     */
	public static Query queryByRegex(final String key, final String regex) {
        Assert.hasText(key, "key 不可为空");
        Assert.hasText(regex, "regex 不可为空");

        return Query.query(Criteria.where(key).regex(regex));
    }

    /**
	 * 根据字段名和 {@link Pattern} 构建匹配查询。
     * <p>
	 * 使用 MongoDB 的正则表达式进行模式匹配查询，可通过 {@link Pattern} 指定大小写、单行/多行等更复杂的匹配选项。
     * </p>
     *
	 * @param key     要查询的字段名，不可为空或空白
	 * @param pattern Java 正则表达式 {@link Pattern} 对象，不可为 {@code null}
     * @return 查询对象
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白，或 {@code pattern} 为 {@code null} 时抛出
     * @since 1.0.0
     */
	public static Query queryByRegex(final String key, final Pattern pattern) {
        Assert.hasText(key, "key 不可为空");
        Assert.notNull(pattern, "pattern 不可为null");

        return Query.query(Criteria.where(key).regex(pattern));
    }

    /**
	 * 根据字段名和正则表达式字符串构建“不匹配”查询。
     * <p>
	 * 使用 MongoDB 的正则表达式进行模式不匹配查询；匹配选项说明参见 {@link #queryByRegex(String, String)}。
     * </p>
     *
	 * @param key   要查询的字段名，不可为空或空白
	 * @param regex 正则表达式字符串，不可为空或空白
     * @return 查询对象
	 * @throws IllegalArgumentException 当 {@code key} 或 {@code regex} 为空或空白时抛出
     * @since 1.0.0
     */
	public static Query queryByNotRegex(final String key, final String regex) {
        Assert.hasText(key, "key 不可为空");
        Assert.hasText(regex, "regex 不可为空");

        return Query.query(Criteria.where(key).not().regex(regex));
    }

    /**
	 * 根据字段名和 {@link Pattern} 构建“不匹配”查询。
     * <p>
	 * 使用 MongoDB 的正则表达式进行模式不匹配查询；可通过 {@link Pattern} 指定更丰富的匹配选项。
     * </p>
     *
	 * @param key     要查询的字段名，不可为空或空白
	 * @param pattern Java 正则表达式 {@link Pattern} 对象，不可为 {@code null}
     * @return 查询对象
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白，或 {@code pattern} 为 {@code null} 时抛出
     * @since 1.0.0
     */
	public static Query queryByNotRegex(final String key, final Pattern pattern) {
        Assert.hasText(key, "key 不可为空");
        Assert.notNull(pattern, "pattern 不可为null");

        return Query.query(Criteria.where(key).not().regex(pattern));
    }

    /**
	 * 构建“字段值为空”的条件。
     * <p>
	 * 使用 {@code $or} 操作符组合：值为 {@code null} 与字段不存在两类条件。
     * </p>
     *
	 * @param key 字段名，不可为空或空白
     * @return 条件对象
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白时抛出
     * @since 1.0.0
     */
	protected static Criteria nullValueCriteria(final String key) {
        Assert.hasText(key, "key 不可为空");

        Criteria nullValueCriteria = Criteria.where(key).isNullValue();
        Criteria nullCriteria = Criteria.where(key).isNull();
        return new Criteria().orOperator(nullValueCriteria, nullCriteria);
    }

    /**
	 * 构建“字段值非空”的条件。
     * <p>
	 * 使用 {@code $and} 操作符组合：值不为 {@code null} 与字段存在两类条件。
     * </p>
     *
	 * @param key 字段名，不可为空或空白
     * @return 条件对象
	 * @throws IllegalArgumentException 当 {@code key} 为空或空白时抛出
     * @since 1.0.0
     */
	protected static Criteria notNullValueCriteria(final String key) {
        Assert.hasText(key, "key 不可为空");

        Criteria notNullValueValueCriteria = Criteria.where(key).not().isNullValue();
        Criteria notNullCriteria = Criteria.where(key).ne(null);
        return new Criteria().andOperator(notNullValueValueCriteria, notNullCriteria);
    }

    /**
	 * 空查询实现类。
     * <p>
	 * 继承自 {@link Query}，所有方法均返回 {@code this} 并不改变查询条件；提供一个安全的“空对象”用于占位或默认参数。
     * </p>
     *
     * @since 1.0.0
     */
    protected static class EmptyQuery extends Query {
        public EmptyQuery() {
        }

        @Override
        public Query addCriteria(CriteriaDefinition criteriaDefinition) {
            return this;
        }

        @Override
        public Query skip(long skip) {
            return this;
        }

        @Override
        public Query limit(Limit limit) {
            return this;
        }

        @Override
        public Query withHint(String hint) {
            return this;
        }

        @Override
        public Query withReadConcern(ReadConcern readConcern) {
            return this;
        }

        @Override
        public Query withReadPreference(ReadPreference readPreference) {
            return this;
        }

        @Override
        public Query withHint(Document hint) {
            return this;
        }

        @Override
        public Query with(Pageable pageable) {
            return this;
        }

        @Override
        public Query with(ScrollPosition position) {
            return this;
        }

        @Override
        public Query with(OffsetScrollPosition position) {
            return this;
        }

        @Override
        public Query with(KeysetScrollPosition position) {
            return this;
        }

        @Override
        public Query with(Sort sort) {
            return this;
        }

        @Override
        public Query restrict(Class<?> type, Class<?>... additionalTypes) {
            return this;
        }

        @Override
        public Query maxTime(Duration timeout) {
            return this;
        }

        @Override
        public Query comment(String comment) {
            return this;
        }

        @Override
        public Query maxTimeMsec(long maxTimeMsec) {
            return this;
        }

        @Override
        public Query allowDiskUse(boolean allowDiskUse) {
            return this;
        }

        @Override
        public Query cursorBatchSize(int batchSize) {
            return this;
        }

        @Override
        public Query noCursorTimeout() {
            return this;
        }

        @Override
        public Query allowSecondaryReads() {
            return this;
        }

        @Override
        public Query partialResults() {
            return this;
        }

        @Override
        public Query exhaust() {
            return this;
        }

        @Override
        public Query collation(Collation collation) {
            return this;
        }

        @Override
        public void setMeta(Meta meta) {
        }

        @Override
        public Query limit(int limit) {
            return this;
        }
    }
}
