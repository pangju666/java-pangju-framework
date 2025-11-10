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

package io.github.pangju666.framework.data.mongodb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * MongoDB 基础仓库接口，继承自 {@link MongoRepository}，在其基础上提供常用读写操作的便捷方法。
 * <p>
 * 使用示例：
 * <pre>{@code
 * @Configuration
 * @EnableMongoRepositories(repositoryBaseClass = SimpleBaseMongoRepository.class)
 * public class MongoConfig {}
 *
 * public interface UserRepository extends BaseMongoRepository<User, String> {}
 * }</pre>
 *
 * @param <T>  实体类型
 * @param <ID> ID类型
 * @author pangju666
 * @see MongoRepository
 * @see EnableMongoRepositories
 * @since 1.0.0
 */
@NoRepositoryBean
public interface BaseMongoRepository<T, ID> extends MongoRepository<T, ID> {
	/**
	 * 判断是否存在满足{@code key}等于{@code value}条件的文档。
	 *
	 * @param key   字段名（不可为空或空白）
	 * @param value 字段值（可为 null）
	 * @return 是否存在匹配文档
	 * @since 1.0.0
	 */
	boolean existsByKeyValue(String key, @Nullable Object value);

	/**
	 * 判断是否存在满足给定{@link Query}条件的文档。
	 *
	 * @param query 查询条件（可为 null）
	 * @return 是否存在匹配文档
	 * @since 1.0.0
	 */
	boolean exists(Query query);

	/**
	 * 查找并返回一个满足{@code key}等于{@code value}的文档。
	 *
	 * @param key   字段名（不可为空或空白）
	 * @param value 字段值（可为 null）
	 * @return 匹配文档的 {@link Optional}
	 * @since 1.0.0
	 */
	Optional<T> findOneByKeyValue(String key, @Nullable Object value);

	/**
	 * 查找并返回一个满足给定{@link Query}条件的文档。
	 *
	 * @param query 查询条件（可为 null）
	 * @return 匹配文档的 {@link Optional}
	 * @since 1.0.0
	 */
	Optional<T> findOne(Query query);

	/**
	 * 统计满足给定{@link Query}条件的文档数量。
	 *
	 * @param query 查询条件（可为 null）
	 * @return 匹配文档数量
	 * @since 1.0.0
	 */
	long count(Query query);

	/**
	 * 提取指定字段的去重值列表。
	 *
	 * @param key        字段名（不可为空或空白）
	 * @param valueClass 结果值类型（不可为 null）
	 * @param <V>        值类型
	 * @return 去重后的字段值列表
	 * @since 1.0.0
	 */
	<V> List<V> findDistinctKeyValues(String key, Class<V> valueClass);

	/**
	 * 在给定查询条件下，提取指定字段的去重值列表。
	 *
	 * @param query      查询条件（可为 null）
	 * @param key        字段名（不可为空或空白）
	 * @param valueClass 结果值类型（不可为 null）
	 * @param <V>        值类型
	 * @return 去重后的字段值列表
	 * @since 1.0.0
	 */
	<V> List<V> findDistinctKeyValues(Query query, String key, Class<V> valueClass);

	/**
	 * 查询所有满足给定{@link Query}条件的文档列表。
	 *
	 * @param query 查询条件（可为 null）
	 * @return 匹配文档列表
	 * @since 1.0.0
	 */
	List<T> findAll(Query query);

	/**
	 * 查询所有满足{@code key}等于{@code value}的文档列表。
	 *
	 * @param key   字段名（不可为空或空白）
	 * @param value 字段值（可为 null）
	 * @return 匹配文档列表
	 * @since 1.0.0
	 */
	List<T> findAllByKeyValue(String key, @Nullable Object value);

	/**
	 * 查询所有满足{@code key}不等于{@code value}的文档列表。
	 *
	 * @param key   字段名（不可为空或空白）
	 * @param value 字段值（可为 null）
	 * @return 匹配文档列表
	 * @since 1.0.0
	 */
	List<T> findAllByKeyNotValue(String key, @Nullable Object value);

	/**
	 * 查询所有满足{@code key}在给定集合中的文档列表。
	 *
	 * @param key    字段名（不可为空或空白）
	 * @param values 值集合（可为空或空集合）
	 * @return 匹配文档列表
	 * @since 1.0.0
	 */
	List<T> findAllByKeyValues(String key, Iterable<?> values);

	/**
	 * 查询所有满足{@code key}不在给定集合中的文档列表。
	 *
	 * @param key    字段名（不可为空或空白）
	 * @param values 值集合（可为空或空集合）
	 * @return 匹配文档列表
	 * @since 1.0.0
	 */
	List<T> findAllByKeyNotValues(String key, Iterable<?> values);

	/**
	 * 查询所有满足{@code key}为{@code null}的文档列表。
	 *
	 * @param key 字段名（不可为空或空白）
	 * @return 匹配文档列表
	 * @since 1.0.0
	 */
	List<T> findAllByKeyNull(String key);

	/**
	 * 查询所有满足{@code key}非{@code null}的文档列表。
	 *
	 * @param key 字段名（不可为空或空白）
	 * @return 匹配文档列表
	 * @since 1.0.0
	 */
	List<T> findAllByKeyNotNull(String key);

	/**
	 * 查询所有满足{@code key}不匹配指定正则表达式的文档列表。
	 *
	 * @param key   字段名（不可为空或空白）
	 * @param regex 正则表达式字符串（不可为空或空白）
	 * @return 匹配文档列表
	 * @since 1.0.0
	 */
	List<T> findAllByKeyNotRegex(String key, String regex);

	/**
	 * 查询所有满足{@code key}不匹配指定正则表达式的文档列表。
	 *
	 * @param key     字段名（不可为空或空白）
	 * @param pattern 正则表达式模式（不可为 null）
	 * @return 匹配文档列表
	 * @since 1.0.0
	 */
	List<T> findAllByKeyNotRegex(String key, Pattern pattern);

	/**
	 * 查询所有满足{@code key}匹配指定正则表达式的文档列表。
	 *
	 * @param key   字段名（不可为空或空白）
	 * @param regex 正则表达式字符串（不可为空或空白）
	 * @return 匹配文档列表
	 * @since 1.0.0
	 */
	List<T> findAllByKeyRegex(String key, String regex);

	/**
	 * 查询所有满足{@code key}匹配指定正则表达式的文档列表。
	 *
	 * @param key     字段名（不可为空或空白）
	 * @param pattern 正则表达式模式（不可为 null）
	 * @return 匹配文档列表
	 * @since 1.0.0
	 */
	List<T> findAllByKeyRegex(String key, Pattern pattern);

	/**
	 * 在给定{@link Query}条件下执行分页查询。
	 *
	 * @param pageable 分页与排序参数（不可为 null）
	 * @param query    查询条件（可为 null）
	 * @return 分页结果
	 * @since 1.0.0
	 */
	Page<T> findAll(Pageable pageable, Query query);

	/**
	 * 对指定{@code id}的文档执行更新。
	 *
	 * @param update 更新定义（不可为 null）
	 * @param id     文档标识（不可为 null）
	 * @since 1.0.0
	 */
	void updateById(UpdateDefinition update, ID id);

	/**
	 * 对给定{@code ids}集合中的文档批量执行更新。
	 *
	 * @param update 更新定义（不可为 null）
	 * @param ids    文档标识集合（可为空或空集合）
	 * @since 1.0.0
	 */
	void updateAllById(UpdateDefinition update, Iterable<ID> ids);

	/**
	 * 根据{@code key}等于{@code value}条件批量更新文档。
	 *
	 * @param update 更新定义（不可为 null）
	 * @param key    字段名（不可为空或空白）
	 * @param value  字段值（可为 null）
	 * @since 1.0.0
	 */
	void updateAllByKeyValue(UpdateDefinition update, String key, @Nullable Object value);

	/**
	 * 对满足给定{@link Query}条件的文档批量执行更新。
	 *
	 * @param update 更新定义（不可为 null）
	 * @param query  查询条件（可为 null）
	 * @since 1.0.0
	 */
	void updateAll(UpdateDefinition update, Query query);

	/**
	 * 将满足{@code key}等于{@code oldValue}的文档的该字段更新为{@code newValue}。
	 *
	 * @param key      字段名（不可为空或空白）
	 * @param oldValue 旧值（可为 null）
	 * @param newValue 新值（可为 null）
	 * @param <V>      字段值类型
	 * @since 1.0.0
	 */
	<V> void replaceKeyValue(String key, @Nullable V oldValue, @Nullable V newValue);

	/**
	 * 删除所有满足{@code key}等于{@code value}条件的文档。
	 *
	 * @param key   字段名（不可为空或空白）
	 * @param value 字段值（可为 null）
	 * @since 1.0.0
	 */
	void deleteAllByKeyValue(String key, @Nullable Object value);

	/**
	 * 删除所有满足给定{@link Query} 条件的文档。
	 *
	 * @param query 查询条件（可为 null）
	 * @since 1.0.0
	 */
	void deleteAll(Query query);
}