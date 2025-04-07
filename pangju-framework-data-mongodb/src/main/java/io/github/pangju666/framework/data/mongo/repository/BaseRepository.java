package io.github.pangju666.framework.data.mongo.repository;

import com.mongodb.client.result.DeleteResult;
import io.github.pangju666.commons.lang.utils.ReflectionUtils;
import io.github.pangju666.commons.lang.utils.StringUtils;
import io.github.pangju666.framework.data.mongo.model.BasicDocument;
import io.github.pangju666.framework.data.mongo.pool.MongoConstants;
import org.apache.commons.lang3.Validate;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseRepository<T extends BasicDocument> {
	protected Class<T> entityClass;
	protected MongoOperations mongoOperations;
	protected String collectionName;

	protected BaseRepository(MongoOperations mongoOperations) {
		setMongoOperations(mongoOperations);
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		Assert.notNull(mongoOperations, "mongoOperations 不可为null");

		this.mongoOperations = mongoOperations;
		this.entityClass = ReflectionUtils.getClassGenericType(this.getClass());
		String collectionName = null;
		Document document = this.entityClass.getAnnotation(Document.class);
		if (Objects.nonNull(document)) {
			collectionName = document.value();
			if (StringUtils.isEmpty(collectionName)) {
				collectionName = document.collection();
			}
		}
		if (StringUtils.isBlank(collectionName)) {
			collectionName = this.mongoOperations.getCollectionName(entityClass);
		}
		this.collectionName = collectionName;
	}

	public MongoOperations getMongoOperations() {
		return mongoOperations;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public <V> boolean existByKeyValue(String key, V value) {
		Assert.hasText(key, "key 不可为空");

		if (Objects.isNull(value)) {
			return mongoOperations.exists(Query.query(notNullCriteria(key)), this.entityClass,
				this.collectionName);
		}
		return mongoOperations.exists(Query.query(Criteria.where(key).is(value)), this.entityClass,
			this.collectionName);
	}

	public <V> boolean notExistByKeyValue(String key, V value) {
		Assert.hasText(key, "key 不可为空");

		if (Objects.isNull(value)) {
			return !mongoOperations.exists(Query.query(notNullCriteria(key)), this.entityClass,
				this.collectionName);
		}
		return mongoOperations.exists(Query.query(Criteria.where(key).not().is(value)), this.entityClass,
			this.collectionName);
	}

	public boolean existsById(String id) {
		Assert.hasText(id, "id 不可为空");

		return mongoOperations.exists(queryById(id), this.entityClass, this.collectionName);
	}

	public boolean existsByObjectId(ObjectId id) {
		Assert.notNull(id, "id 不可为null");

		return mongoOperations.exists(queryByObjectId(id), this.entityClass, this.collectionName);
	}

	public boolean exist(Query query) {
		Assert.notNull(query, "query 不可为null");

		return mongoOperations.exists(query, this.entityClass, this.collectionName);
	}

	public <V> T getByKeyValue(String key, V value) {
		Assert.hasText(key, "key 不可为空");

		if (Objects.isNull(value)) {
			return mongoOperations.findOne(Query.query(nullCriteria(key)), this.entityClass, this.collectionName);
		}
		return mongoOperations.findOne(Query.query(Criteria.where(key).is(value)), this.entityClass, this.collectionName);
	}

	public T getById(String id) {
		Assert.hasText(id, "id 不可为空");

		return mongoOperations.findById(id, this.entityClass, this.collectionName);
	}

	public T getByObjectId(ObjectId id) {
		Assert.notNull(id, "id 不可为null");

		return mongoOperations.findById(id, this.entityClass, this.collectionName);
	}

	public T getOne(Query query) {
		Assert.notNull(query, "query 不可为null");

		return mongoOperations.findOne(query, this.entityClass, this.collectionName);
	}

	public long count() {
		return mongoOperations.count(new Query(), this.collectionName);
	}

	public long count(Query query) {
		Assert.notNull(query, "query 不可为null");

		return mongoOperations.count(query, this.collectionName);
	}

	public List<T> listByIds(Collection<String> ids) {
		if (Objects.isNull(ids) || ids.isEmpty()) {
			return Collections.emptyList();
		}
		return mongoOperations.find(queryByIds(ids), this.entityClass, this.collectionName);
	}

	public List<T> listByObjectIds(Collection<ObjectId> ids) {
		if (Objects.isNull(ids) || ids.isEmpty()) {
			return Collections.emptyList();
		}
		return mongoOperations.find(queryByObjectIds(ids), this.entityClass, this.collectionName);
	}

	public List<T> list() {
		return mongoOperations.findAll(this.entityClass, this.collectionName);
	}

	public List<T> list(Sort sort) {
		Assert.notNull(sort, "sort 不可为null");

		return mongoOperations.find(new Query().with(sort), this.entityClass, this.collectionName);
	}

	public List<T> list(Query query) {
		Assert.notNull(query, "query 不可为null");

		return mongoOperations.find(query, this.entityClass, this.collectionName);
	}

	public <V> List<T> listByKeyValue(String key, V value) {
		Validate.notBlank(key, "key 不可为空");

		if (Objects.isNull(value)) {
			return lambdaQuery()
				.isNull(column)
				.list();
		}
		return lambdaQuery()
			.eq(column, value)
			.list();
	}

	public <V> List<T> listByKeyValues(String key, Collection<V> values) {
		Validate.notBlank(key, "key 不可为空");

		return listByColumnValues(lambdaQuery(), column, values, DEFAULT_LIST_BATCH_SIZE);
	}

	public <V> List<T> listByKeyValues(Query query, String key, Collection<V> values) {
		Validate.notBlank(key, "key 不可为空");


		Validate.notNull(column, "column 不可为null");
		Validate.notNull(queryChainWrapper, "queryChainWrapper 不可为null");
		Validate.isTrue(batchSize > 0, "batchSize 必须大于0");

		List<V> validList = CollectionUtils.emptyIfNull(values)
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validList.isEmpty()) {
			return Collections.emptyList();
		}

		if (validList.size() <= batchSize) {
			return queryChainWrapper.in(column, validList).list();
		}
		return ListUtils.partition(validList, batchSize)
			.stream()
			.map(part -> queryChainWrapper.in(column, part).list())
			.flatMap(List::stream)
			.toList();
	}

	public <V> List<T> listByNotNullKey(String key) {
		return listByNotNullColumn(lambdaQuery(), column);
	}

	public <V> List<T> listByNotNullKey(Query query, String key) {
		Validate.notNull(column, "column 不可为null");
		Validate.notNull(queryChainWrapper, "queryChainWrapper 不可为null");

		return queryChainWrapper.isNotNull(column).list();
	}

	public <V> List<T> listByNullKey(String key) {
		return listByNullColumn(lambdaQuery(), column);
	}

	public <V> List<T> listByNullKey(Query query, String key) {
		Validate.notNull(column, "column 不可为null");
		Validate.notNull(queryChainWrapper, "queryChainWrapper 不可为null");

		return queryChainWrapper.isNull(column).list();
	}

	public <V> List<T> listByNullValueKey(String key) {
		return listByNullColumn(lambdaQuery(), column);
	}

	public <V> List<T> listByNullValueKey(Query query, String key) {
		Validate.notNull(column, "column 不可为null");
		Validate.notNull(queryChainWrapper, "queryChainWrapper 不可为null");

		return queryChainWrapper.isNull(column).list();
	}

	public <V> List<T> listByNotNullValueKey(String key) {
		return listByNullColumn(lambdaQuery(), column);
	}

	public <V> List<T> listByNotNullValueKey(Query query, String key) {
		Validate.notNull(column, "column 不可为null");
		Validate.notNull(queryChainWrapper, "queryChainWrapper 不可为null");

		return queryChainWrapper.isNull(column).list();
	}

	public List<T> listByNotRegex(String key, String regex) {
		Validate.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.like(column, value)
			.list();
	}

	public List<T> listByNotRegex(String key, Pattern pattern) {
		Validate.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.notLike(column, value)
			.list();
	}

	public List<T> listByRegex(String key, String regex) {
		Validate.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.like(column, value)
			.list();
	}

	public List<T> listByRegex(String key, Pattern pattern) {
		Validate.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.notLike(column, value)
			.list();
	}

	public List<T> listByNotRegex(Query query, String key, String regex) {
		Validate.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.like(column, value)
			.list();
	}

	public List<T> listByNotRegex(Query query, String key, Pattern pattern) {
		Validate.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.notLike(column, value)
			.list();
	}

	public List<T> listByRegex(Query query, String key, String regex) {
		Validate.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.like(column, value)
			.list();
	}

	public List<T> listByRegex(Query query, String key, Pattern pattern) {
		Validate.notNull(column, "column 不可为null");

		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}
		return lambdaQuery()
			.notLike(column, value)
			.list();
	}


	public Stream<T> streamByIds(Collection<String> ids) {
		if (Objects.isNull(ids) || ids.isEmpty()) {
			return Stream.empty();
		}
		return mongoOperations.stream(queryByIds(ids), this.entityClass, this.collectionName);
	}

	public Stream<T> streamByObjectIds(Collection<ObjectId> ids) {
		if (Objects.isNull(ids) || ids.isEmpty()) {
			return Stream.empty();
		}
		return mongoOperations.stream(queryByObjectIds(ids), this.entityClass, this.collectionName);
	}

	public Stream<T> stream() {
		return mongoOperations.stream(new Query(), this.entityClass, this.collectionName);
	}

	public Stream<T> stream(Sort sort) {
		Assert.notNull(sort, "sort 不可为null");

		return mongoOperations.stream(new Query().with(sort), this.entityClass, this.collectionName);
	}

	public Stream<T> stream(Query query) {
		Assert.notNull(query, "query 不可为null");

		return mongoOperations.stream(query, this.entityClass, this.collectionName);
	}

	public Page<T> page(Pageable pageable) {
		Assert.notNull(pageable, "pageable 不可为null");

		long count = count();
		List<T> list = list(new Query().with(pageable));
		return new PageImpl<>(list, pageable, count);
	}

	public Page<T> page(Pageable pageable, Sort sort) {
		Assert.notNull(pageable, "pageable 不可为null");
		Assert.notNull(sort, "sort 不可为null");

		long count = count();
		List<T> list = list(new Query().with(pageable).with(sort));
		return new PageImpl<>(list, pageable, count);
	}

	public Page<T> page(Pageable pageable, Query query) {
		Assert.notNull(pageable, "pageable 不可为null");
		Assert.notNull(query, "query 不可为null");

		long count = count(query);
		List<T> list = list(query.with(pageable));
		return new PageImpl<>(list, pageable, count);
	}

	public Page<T> page(Pageable pageable, Query query, Sort sort) {
		Assert.notNull(pageable, "pageable 不可为null");
		Assert.notNull(query, "query 不可为null");
		Assert.notNull(sort, "sort 不可为null");

		long count = count(query);
		List<T> list = list(query.with(pageable).with(sort));
		return new PageImpl<>(list, pageable, count);
	}

	public void insert(T entity) {
		mongoOperations.insert(entity, this.collectionName);
	}

	public void insertBatch(Collection<T> entities) {
		if (Objects.nonNull(entities) && !entities.isEmpty()) {
			List<T> validaEntities = entities.stream()
				.filter(Objects::nonNull)
				.toList();
			mongoOperations.insert(validaEntities, this.collectionName);
		}
	}

	public void save(T entity) {
		mongoOperations.save(entity, this.collectionName);
	}

	public void saveBatch(Collection<T> entities) {
		if (Objects.nonNull(entities) && !entities.isEmpty()) {
			List<T> insertEntities = entities.stream()
				.filter(entity -> Objects.nonNull(entity) && Objects.isNull(entity.getId()))
				.toList();
			if (!insertEntities.isEmpty()) {
				mongoOperations.insert(insertEntities, this.collectionName);
			}

			List<T> updateEntities = entities.stream()
				.filter(entity -> Objects.nonNull(entity) && Objects.nonNull(entity.getId()))
				.toList();
			if (!updateEntities.isEmpty()) {
				for (T updateEntity : updateEntities) {
					mongoOperations.save(updateEntity, this.collectionName);
				}
			}
		}
	}

	public boolean removeById(String id) {
		DeleteResult result = mongoOperations.remove(queryById(id), this.entityClass, this.collectionName);
		return result.wasAcknowledged() && result.getDeletedCount() == 1;
	}

	public boolean removeByIds(Collection<String> ids) {
		List<String> validIds = StringUtils.getUniqueNotBlankElements(ids);
		Query query = new Query(new Criteria(MongoConstants.ID_FIELD_NAME).in(validIds));
		DeleteResult result = mongoOperations.remove(query, this.entityClass, this.collectionName);
		return result.wasAcknowledged() && result.getDeletedCount() == validIds.size();
	}

	public boolean removeByObjectId(ObjectId id) {
		DeleteResult result = mongoOperations.remove(queryByObjectId(id), this.entityClass, this.collectionName);
		return result.wasAcknowledged() && result.getDeletedCount() == 1;
	}

	public boolean removeByObjectIds(Collection<ObjectId> ids) {
		Set<String> validIds = ids.stream()
			.filter(Objects::nonNull)
			.map(ObjectId::toHexString)
			.collect(Collectors.toSet());
		Query query = new Query(new Criteria(MongoConstants.ID_FIELD_NAME).in(validIds));
		DeleteResult result = mongoOperations.remove(query, this.entityClass, this.collectionName);
		return result.wasAcknowledged() && result.getDeletedCount() == validIds.size();
	}

	public boolean remove(Query query) {
		DeleteResult result = mongoOperations.remove(query, this.entityClass, this.collectionName);
		return result.wasAcknowledged() && result.getDeletedCount() > 0;
	}

	protected Query queryByObjectId(ObjectId id) {
		Criteria criteria = criteriaById(id.toHexString());
		return new Query(criteria);
	}

	protected Query queryById(String id) {
		Criteria criteria = criteriaById(id);
		return new Query(criteria);
	}

	protected Query queryByIds(Collection<String> ids) {
		List<String> validIds = StringUtils.getUniqueNotBlankElements(ids);
		return new Query(new Criteria(MongoConstants.ID_FIELD_NAME).in(validIds));
	}

	protected Query queryByObjectIds(Collection<ObjectId> ids) {
		Set<String> validIds = ids.stream()
			.filter(Objects::nonNull)
			.map(ObjectId::toHexString)
			.collect(Collectors.toSet());
		return new Query(new Criteria(MongoConstants.ID_FIELD_NAME).in(validIds));
	}

	protected Criteria criteriaById(String id) {
		return new Criteria(MongoConstants.ID_FIELD_NAME).is(id);
	}

	protected Criteria nullCriteria(String key) {
		Criteria nullValueCriteria = Criteria.where(key).isNullValue();
		Criteria nullCriteria = Criteria.where(key).isNull();
		return nullValueCriteria.orOperator(nullCriteria);
	}

	protected Criteria notNullCriteria(String key) {
		Criteria nullValueCriteria = Criteria.where(key).not().isNullValue();
		Criteria nullCriteria = Criteria.where(key).not().isNull();
		return nullValueCriteria.orOperator(nullCriteria);
	}
}