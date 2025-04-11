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
import io.github.pangju666.framework.data.mongodb.pool.MongoConstants;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Collection;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * MongoDB查询工具类
 * <p>
 * 提供一系列静态方法用于构建{@link Query MongoDB查询对象}，主要功能包括：
 * <ul>
 *     <li>空查询构建</li>
 *     <li>ID查询构建</li>
 *     <li>字段值查询构建</li>
 *     <li>正则表达式查询构建</li>
 *     <li>空值和非空值查询构建</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
public class QueryUtils {
    /**
     * 空查询对象常量
     * <p>
     * 该查询对象的所有操作方法都返回自身，不会改变查询条件
     * </p>
     *
     * @since 1.0.0
     */
    public static final Query EMPTY_QUERY = new EmptyQuery();

    protected QueryUtils() {
    }

    /**
     * 获取空查询对象
     *
     * @return 空查询对象
     * @since 1.0.0
     */
    public static Query emptyQuery() {
        return EMPTY_QUERY;
    }

    /**
     * 构建字段值为null的查询
     * <p>
     * 使用$or操作符组合以下条件：
     * <ul>
     *     <li>字段值为null</li>
     *     <li>字段不存在</li>
     * </ul>
     * </p>
     *
     * @param key 字段名
     * @return 查询对象
     * @throws IllegalArgumentException 当key为空时抛出
     * @since 1.0.0
     */
    public static Query queryByKeyNullValue(String key) {
        return Query.query(keyNullValueCriteria(key));
    }

    /**
     * 构建字段值不为null的查询
     * <p>
     * 使用$and操作符组合以下条件：
     * <ul>
     *     <li>字段值不为null</li>
     *     <li>字段存在</li>
     * </ul>
     * </p>
     *
     * @param key 字段名
     * @return 查询对象
     * @throws IllegalArgumentException 当key为空时抛出
     * @since 1.0.0
     */
    public static Query queryByKeyNotNullValue(String key) {
        return Query.query(keyNotNullValueCriteria(key));
    }

    /**
     * 根据ID构建查询
     *
     * @param id 文档ID
     * @return 查询对象
     * @throws IllegalArgumentException 当id为空时抛出
     * @since 1.0.0
     */
    public static Query queryById(String id) {
        Assert.hasText(id, "id 不可为空");

        return Query.query(Criteria.where(MongoConstants.ID_FIELD_NAME).is(id));
    }

    /**
     * 根据ObjectId构建查询
     *
     * @param id MongoDB的ObjectId
     * @return 查询对象
     * @throws IllegalArgumentException 当id为null时抛出
     * @since 1.0.0
     */
    public static Query queryById(ObjectId id) {
        Assert.notNull(id, "id 不可为null");

        return Query.query(Criteria.where(MongoConstants.ID_FIELD_NAME).is(id.toHexString()));
    }

    /**
     * 根据ID集合构建包含查询
     * <p>
     * 使用MongoDB的$in操作符查询ID在指定集合中的文档
     * </p>
     *
     * @param ids ID集合
     * @return 查询对象
     * @throws IllegalArgumentException 当ids为空时抛出
     * @since 1.0.0
     */
    public static Query queryByIds(Collection<String> ids) {
        Assert.notEmpty(ids, "id 不可为空");

        return Query.query(Criteria.where(MongoConstants.ID_FIELD_NAME).in(ids));
    }

    /**
     * 根据ID集合构建排除查询
     * <p>
     * 使用MongoDB的$nin操作符查询ID不在指定集合中的文档
     * </p>
     *
     * @param ids ID集合
     * @return 查询对象
     * @throws IllegalArgumentException 当ids为空时抛出
     * @since 1.0.0
     */
    public static Query queryByNotIds(Collection<String> ids) {
        Assert.notEmpty(ids, "id 不可为空");

        return Query.query(Criteria.where(MongoConstants.ID_FIELD_NAME).nin(ids));
    }

    /**
     * 根据字段名和值构建等值查询
     * <p>
     * <ul>
     *     <li>当value为null时，查询字段值为null或字段不存在的文档</li>
     *     <li>当value不为null时，查询字段值等于value的文档</li>
     * </ul>
     * </p>
     *
     * @param key   要查询的字段名
     * @param value 要查询的字段值
     * @return 查询对象
     * @throws IllegalArgumentException 当key为空时抛出
     * @since 1.0.0
     */
    public static Query queryByKeyValue(String key, Object value) {
        if (Objects.isNull(value)) {
            return Query.query(keyNullValueCriteria(key));
        }

        Assert.hasText(key, "key 不可为空");

        return Query.query(Criteria.where(key).is(value));
    }

    /**
     * 根据字段名和值构建不等值查询
     * <p>
     * <ul>
     *     <li>当value为null时，查询字段值不为null且字段存在的文档</li>
     *     <li>当value不为null时，查询字段值不等于value的文档</li>
     * </ul>
     * </p>
     *
     * @param key   要查询的字段名
     * @param value 要排除的字段值
     * @return 查询对象
     * @throws IllegalArgumentException 当key为空时抛出
     * @since 1.0.0
     */
    public static Query queryByKeyNotValue(String key, Object value) {
        if (Objects.isNull(value)) {
            return Query.query(keyNotNullValueCriteria(key));
        }

        Assert.hasText(key, "key 不可为空");

        return Query.query(Criteria.where(key).ne(value));
    }

    /**
     * 根据字段名和值集合构建包含查询
     * <p>
     * 使用MongoDB的$in操作符查询字段值在指定集合中的文档
     * </p>
     *
     * @param key    要查询的字段名
     * @param values 要匹配的值集合
     * @return 查询对象
     * @throws IllegalArgumentException 当key为空或values为空时抛出
     * @since 1.0.0
     */
    public static Query queryByKeyValues(String key, Collection<?> values) {
        Assert.hasText(key, "key 不可为空");
        Assert.notEmpty(values, "values 不可为空");

        return Query.query(Criteria.where(key).in(values));
    }

    /**
     * 根据字段名和值集合构建排除查询
     * <p>
     * 使用MongoDB的$nin操作符查询字段值不在指定集合中的文档
     * </p>
     *
     * @param key    要查询的字段名
     * @param values 要排除的值集合
     * @return 查询对象
     * @throws IllegalArgumentException 当key为空或values为空时抛出
     * @since 1.0.0
     */
    public static Query queryByKeyNotValues(String key, Collection<?> values) {
        Assert.hasText(key, "key 不可为空");
        Assert.notEmpty(values, "values 不可为空");

        return Query.query(Criteria.where(key).nin(values));
    }

    /**
     * 根据字段名和正则表达式字符串构建匹配查询
     * <p>
     * 使用MongoDB的正则表达式功能进行模式匹配查询
     * </p>
     *
     * @param key   要查询的字段名
     * @param regex 正则表达式字符串
     * @return 查询对象
     * @throws IllegalArgumentException 当key为空或regex为空时抛出
     * @since 1.0.0
     */
    public static Query queryByRegex(String key, String regex) {
        Assert.hasText(key, "key 不可为空");
        Assert.hasText(regex, "regex 不可为空");

        return Query.query(Criteria.where(key).regex(regex));
    }

    /**
     * 根据字段名和Pattern对象构建匹配查询
     * <p>
     * 使用MongoDB的正则表达式功能进行模式匹配查询，支持更复杂的匹配选项
     * </p>
     *
     * @param key     要查询的字段名
     * @param pattern Java正则表达式Pattern对象
     * @return 查询对象
     * @throws IllegalArgumentException 当key为空或pattern为null时抛出
     * @since 1.0.0
     */
    public static Query queryByRegex(String key, Pattern pattern) {
        Assert.hasText(key, "key 不可为空");
        Assert.notNull(pattern, "pattern 不可为null");

        return Query.query(Criteria.where(key).regex(pattern));
    }

    /**
     * 根据字段名和正则表达式字符串构建不匹配查询
     * <p>
     * 使用MongoDB的正则表达式功能进行模式不匹配查询
     * </p>
     *
     * @param key   要查询的字段名
     * @param regex 正则表达式字符串
     * @return 查询对象
     * @throws IllegalArgumentException 当key为空或regex为空时抛出
     * @since 1.0.0
     */
    public static Query queryByNotRegex(String key, String regex) {
        Assert.hasText(key, "key 不可为空");
        Assert.hasText(regex, "regex 不可为空");

        return Query.query(Criteria.where(key).not().regex(regex));
    }

    /**
     * 根据字段名和Pattern对象构建不匹配查询
     * <p>
     * 使用MongoDB的正则表达式功能进行模式不匹配查询，支持更复杂的匹配选项
     * </p>
     *
     * @param key     要查询的字段名
     * @param pattern Java正则表达式Pattern对象
     * @return 查询对象
     * @throws IllegalArgumentException 当key为空或pattern为null时抛出
     * @since 1.0.0
     */
    public static Query queryByNotRegex(String key, Pattern pattern) {
        Assert.hasText(key, "key 不可为空");
        Assert.notNull(pattern, "pattern 不可为null");

        return Query.query(Criteria.where(key).not().regex(pattern));
    }

    /**
     * 构建字段值为null的条件
     * <p>
     * 使用$or操作符组合"值为null"和"字段不存在"两个条件
     * </p>
     *
     * @param key 字段名
     * @return 条件对象
     * @throws IllegalArgumentException 当key为空时抛出
     * @since 1.0.0
     */
    protected static Criteria keyNullValueCriteria(String key) {
        Assert.hasText(key, "key 不可为空");

        Criteria nullValueCriteria = Criteria.where(key).isNullValue();
        Criteria nullCriteria = Criteria.where(key).isNull();
        return nullValueCriteria.orOperator(nullCriteria);
    }

    /**
     * 构建字段值不为null的条件
     * <p>
     * 使用$and操作符组合"值不为null"和"字段存在"两个条件
     * </p>
     *
     * @param key 字段名
     * @return 条件对象
     * @throws IllegalArgumentException 当key为空时抛出
     * @since 1.0.0
     */
    protected static Criteria keyNotNullValueCriteria(String key) {
        Assert.hasText(key, "key 不可为空");

        Criteria notNullValueValueCriteria = Criteria.where(key).not().isNullValue();
        Criteria notNullCriteria = Criteria.where(key).ne(null);
        return notNullValueValueCriteria.andOperator(notNullCriteria);
    }

    /**
     * 空查询实现类
     * <p>
     * 继承自Query类，所有方法都返回this，不执行任何实际操作。
     * 用于需要传入查询对象但不需要实际查询条件的场景。
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
