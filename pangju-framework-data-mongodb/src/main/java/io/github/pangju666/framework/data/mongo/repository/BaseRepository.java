package io.github.pangju666.framework.data.mongo.repository;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.github.pangju666.commons.lang.utils.ReflectionUtils;
import io.github.pangju666.commons.lang.utils.StringUtils;
import io.github.pangju666.framework.data.mongo.pool.MongoConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public abstract class BaseRepository<T> {
	protected Class<T> entityClass;
	protected MongoOperations mongoOperations;
	protected String collectionName;

	private BaseRepository() {
		this.entityClass = ReflectionUtils.getClassGenericType(this.getClass());
	}

	protected BaseRepository(MongoOperations mongoOperations) {
		this();
		setMongoOperations(mongoOperations);
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		Assert.notNull(mongoOperations, "mongoOperations 不可为null");

		this.mongoOperations = mongoOperations;

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

		return mongoOperations.exists(idQuery(id), this.entityClass, this.collectionName);
	}

	public boolean existsByObjectId(ObjectId id) {
		Assert.notNull(id, "id 不可为null");

		return mongoOperations.exists(objectIdQuery(id), this.entityClass, this.collectionName);
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
		List<String> validIds = CollectionUtils.emptyIfNull(ids)
			.stream()
			.filter(StringUtils::isNotBlank)
			.toList();
		if (validIds.isEmpty()) {
			return Collections.emptyList();
		}
		return mongoOperations.find(Query.query(Criteria.where(MongoConstants.ID_FIELD_NAME)
			.in(validIds)), this.entityClass, this.collectionName);
	}

	public List<T> listByObjectIds(Collection<ObjectId> objectIds) {
		List<String> validIds = CollectionUtils.emptyIfNull(objectIds)
			.stream()
			.filter(Objects::nonNull)
			.map(ObjectId::toHexString)
			.toList();
		if (validIds.isEmpty()) {
			return Collections.emptyList();
		}
		return mongoOperations.find(Query.query(Criteria.where(MongoConstants.ID_FIELD_NAME)
			.in(validIds)), this.entityClass, this.collectionName);
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
		Assert.hasText(key, "key 不可为空");

		if (Objects.isNull(value)) {
			return mongoOperations.find(Query.query(nullCriteria(key)), this.entityClass, this.collectionName);
		}
		return mongoOperations.find(Query.query(Criteria.where(key).is(value)), this.entityClass, this.collectionName);
	}

	public <V> List<T> listByKeyValues(String key, Collection<V> values) {
		Assert.hasText(key, "key 不可为空");

		List<V> validValues = CollectionUtils.emptyIfNull(values)
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validValues.isEmpty()) {
			return Collections.emptyList();
		}
		return mongoOperations.find(Query.query(Criteria.where(key).in(validValues)), this.entityClass,
			this.collectionName);
	}

	public <V> List<T> listByKeyValues(Query query, String key, Collection<V> values) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		List<V> validValues = CollectionUtils.emptyIfNull(values)
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validValues.isEmpty()) {
			return Collections.emptyList();
		}
		return mongoOperations.find(query.addCriteria(Criteria.where(key).in(validValues)),
			this.entityClass, this.collectionName);
	}

	public List<T> listByNullValue(String key) {
		Assert.hasText(key, "key 不可为空");

		return mongoOperations.find(Query.query(nullCriteria(key)), this.entityClass, this.collectionName);
	}

	public List<T> listByNullValue(Query query, String key) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		return mongoOperations.find(query.addCriteria(nullCriteria(key)), this.entityClass,
			this.collectionName);
	}

	public List<T> listByNotNullValue(String key) {
		Assert.hasText(key, "key 不可为空");

		return mongoOperations.find(Query.query(notNullCriteria(key)), this.entityClass, this.collectionName);
	}

	public List<T> listByNotNullValue(Query query, String key) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		return mongoOperations.find(query.addCriteria(notNullCriteria(key)), this.entityClass,
			this.collectionName);
	}

	public List<T> listByNotRegex(String key, String regex) {
		Assert.hasText(key, "key 不可为空");

		if (StringUtils.isEmpty(regex)) {
			return Collections.emptyList();
		}
		return mongoOperations.find(Query.query(Criteria.where(key).not().regex(regex)), this.entityClass,
			this.collectionName);
	}

	public List<T> listByNotRegex(String key, Pattern pattern) {
		Assert.hasText(key, "key 不可为空");

		if (Objects.isNull(pattern)) {
			return Collections.emptyList();
		}
		return mongoOperations.find(Query.query(Criteria.where(key).not().regex(pattern)), this.entityClass,
			this.collectionName);
	}

	public List<T> listByNotRegex(Query query, String key, String regex) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		if (StringUtils.isEmpty(regex)) {
			return Collections.emptyList();
		}
		return mongoOperations.find(query.addCriteria(Criteria.where(key).not().regex(regex)), this.entityClass,
			this.collectionName);
	}

	public List<T> listByNotRegex(Query query, String key, Pattern pattern) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		if (Objects.isNull(pattern)) {
			return Collections.emptyList();
		}
		return mongoOperations.find(query.addCriteria(Criteria.where(key).not().regex(pattern)), this.entityClass,
			this.collectionName);
	}

	public List<T> listByRegex(String key, String regex) {
		Assert.hasText(key, "key 不可为空");

		if (StringUtils.isEmpty(regex)) {
			return Collections.emptyList();
		}
		return mongoOperations.find(Query.query(Criteria.where(key).regex(regex)), this.entityClass,
			this.collectionName);
	}

	public List<T> listByRegex(String key, Pattern pattern) {
		Assert.hasText(key, "key 不可为空");

		if (Objects.isNull(pattern)) {
			return Collections.emptyList();
		}
		return mongoOperations.find(Query.query(Criteria.where(key).regex(pattern)), this.entityClass,
			this.collectionName);
	}

	public List<T> listByRegex(Query query, String key, String regex) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		if (StringUtils.isEmpty(regex)) {
			return Collections.emptyList();
		}
		return mongoOperations.find(query.addCriteria(Criteria.where(key).regex(regex)), this.entityClass,
			this.collectionName);
	}

	public List<T> listByRegex(Query query, String key, Pattern pattern) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		if (Objects.isNull(pattern)) {
			return Collections.emptyList();
		}
		return mongoOperations.find(query.addCriteria(Criteria.where(key).regex(pattern)), this.entityClass,
			this.collectionName);
	}

	public Stream<T> streamByIds(Collection<String> ids) {
		List<String> validIds = CollectionUtils.emptyIfNull(ids)
			.stream()
			.filter(StringUtils::isNotBlank)
			.toList();
		if (validIds.isEmpty()) {
			return Stream.empty();
		}
		return mongoOperations.stream(Query.query(Criteria.where(MongoConstants.ID_FIELD_NAME)
			.in(validIds)), this.entityClass, this.collectionName);
	}

	public Stream<T> streamByObjectIds(Collection<ObjectId> objectIds) {
		List<String> validIds = CollectionUtils.emptyIfNull(objectIds)
			.stream()
			.filter(Objects::nonNull)
			.map(ObjectId::toHexString)
			.toList();
		if (validIds.isEmpty()) {
			return Stream.empty();
		}
		return mongoOperations.stream(Query.query(Criteria.where(MongoConstants.ID_FIELD_NAME)
			.in(validIds)), this.entityClass, this.collectionName);
	}

	public <V> Stream<T> streamByKeyValue(String key, V value) {
		Assert.hasText(key, "key 不可为空");

		if (Objects.isNull(value)) {
			return mongoOperations.stream(Query.query(nullCriteria(key)), this.entityClass,
				this.collectionName);
		}
		return mongoOperations.stream(Query.query(Criteria.where(key).is(value)),
			this.entityClass, this.collectionName);
	}

	public <V> Stream<T> streamByKeyValues(String key, Collection<V> values) {
		Assert.hasText(key, "key 不可为空");

		List<V> validValues = CollectionUtils.emptyIfNull(values)
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validValues.isEmpty()) {
			return Stream.empty();
		}
		return mongoOperations.stream(Query.query(Criteria.where(key).in(validValues)),
			this.entityClass, this.collectionName);
	}

	public <V> Stream<T> streamByKeyValues(Query query, String key, Collection<V> values) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		List<V> validValues = CollectionUtils.emptyIfNull(values)
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validValues.isEmpty()) {
			return Stream.empty();
		}
		return mongoOperations.stream(query.addCriteria(Criteria.where(key).in(validValues)),
			this.entityClass, this.collectionName);
	}

	public Stream<T> streamByNotRegex(String key, String regex) {
		Assert.hasText(key, "key 不可为空");

		if (StringUtils.isEmpty(regex)) {
			return Stream.empty();
		}
		return mongoOperations.stream(Query.query(Criteria.where(key).not().regex(regex)),
			this.entityClass, this.collectionName);
	}

	public Stream<T> streamByNotRegex(String key, Pattern pattern) {
		Assert.hasText(key, "key 不可为空");

		if (Objects.isNull(pattern)) {
			return Stream.empty();
		}
		return mongoOperations.stream(Query.query(Criteria.where(key).not().regex(pattern)),
			this.entityClass, this.collectionName);
	}

	public Stream<T> streamByNotRegex(Query query, String key, String regex) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		if (StringUtils.isEmpty(regex)) {
			return Stream.empty();
		}
		return mongoOperations.stream(query.addCriteria(Criteria.where(key).not().regex(regex)),
			this.entityClass, this.collectionName);
	}

	public Stream<T> streamByNotRegex(Query query, String key, Pattern pattern) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		if (Objects.isNull(pattern)) {
			return Stream.empty();
		}
		return mongoOperations.stream(query.addCriteria(Criteria.where(key).not().regex(pattern)),
			this.entityClass, this.collectionName);
	}

	public Stream<T> streamByRegex(String key, String regex) {
		Assert.hasText(key, "key 不可为空");

		if (StringUtils.isEmpty(regex)) {
			return Stream.empty();
		}
		return mongoOperations.stream(Query.query(Criteria.where(key).regex(regex)), this.entityClass,
			this.collectionName);
	}

	public Stream<T> streamByRegex(String key, Pattern pattern) {
		Assert.hasText(key, "key 不可为空");

		if (Objects.isNull(pattern)) {
			return Stream.empty();
		}
		return mongoOperations.stream(Query.query(Criteria.where(key).regex(pattern)),
			this.entityClass, this.collectionName);
	}

	public Stream<T> streamByRegex(Query query, String key, String regex) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		if (StringUtils.isEmpty(regex)) {
			return Stream.empty();
		}
		return mongoOperations.stream(query.addCriteria(Criteria.where(key).regex(regex)),
			this.entityClass, this.collectionName);
	}

	public Stream<T> streamByRegex(Query query, String key, Pattern pattern) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		if (Objects.isNull(pattern)) {
			return Stream.empty();
		}
		return mongoOperations.stream(query.addCriteria(Criteria.where(key).regex(pattern)),
			this.entityClass, this.collectionName);
	}

	public Stream<T> streamByNullValue(String key) {
		Assert.hasText(key, "key 不可为空");

		return mongoOperations.stream(Query.query(nullCriteria(key)), this.entityClass,
			this.collectionName);
	}

	public Stream<T> streamByNullValue(Query query, String key) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		return mongoOperations.stream(query.addCriteria(nullCriteria(key)),
			this.entityClass, this.collectionName);
	}

	public Stream<T> streamByNotNullValue(String key) {
		Assert.hasText(key, "key 不可为空");

		return mongoOperations.stream(Query.query(notNullCriteria(key)), this.entityClass,
			this.collectionName);
	}

	public Stream<T> streamByNotNullValue(Query query, String key) {
		Assert.notNull(query, "query 不可为null");
		Assert.hasText(key, "key 不可为空");

		return mongoOperations.stream(query.addCriteria(notNullCriteria(key)),
			this.entityClass, this.collectionName);
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

	public T insert(T entity) {
		Assert.notNull(entity, "entity 不可为null");

		return mongoOperations.insert(entity, this.collectionName);
	}

	public Collection<T> insertBatch(Collection<T> entities) {
		List<T> validaEntities = CollectionUtils.emptyIfNull(entities)
			.stream()
			.filter(Objects::nonNull)
			.toList();
		if (validaEntities.isEmpty()) {
			return Collections.emptyList();
		}
		return mongoOperations.insert(validaEntities, this.collectionName);
	}

	public void save(T entity) {
		Assert.notNull(entity, "entity 不可为null");

		mongoOperations.save(entity, this.collectionName);
	}

	public Collection<T> saveBatch(Collection<T> entities) {
		return CollectionUtils.emptyIfNull(entities)
			.stream()
			.filter(Objects::nonNull)
			.map(validaEntity -> mongoOperations.save(validaEntity, this.collectionName))
			.toList();
	}

	public <V> boolean updateKeyValueById(String key, V value, String id) {
		Assert.hasText(key, "key 不可为空");
		Assert.hasText(id, "id 不可为空");

		UpdateResult result = mongoOperations.updateFirst(idQuery(id), new Update().set(key, value),
			this.collectionName);
		return result.wasAcknowledged() && result.getModifiedCount() == 1;
	}

	public <V> boolean updateKeyValueByObjectId(String key, V value, ObjectId objectId) {
		Assert.hasText(key, "key 不可为空");
		Assert.notNull(objectId, "objectId 不可为null");

		UpdateResult result = mongoOperations.updateFirst(objectIdQuery(objectId), new Update().set(key, value),
			this.collectionName);
		return result.wasAcknowledged() && result.getModifiedCount() == 1;
	}

	public <V> long replaceKeyValue(String key, V newValue, V oldValue) {
		Assert.hasText(key, "key 不可为空");

		UpdateResult result;
		if (Objects.isNull(oldValue)) {
			result = mongoOperations.updateMulti(Query.query(nullCriteria(key)),
				new Update().set(key, newValue), this.collectionName);
		} else {
			result = mongoOperations.updateMulti(Query.query(Criteria.where(key).is(oldValue)),
				new Update().set(key, newValue), this.collectionName);
		}
		return result.wasAcknowledged() ? result.getModifiedCount() : 0;
	}

	public boolean removeById(String id) {
		Assert.hasText(id, "id 不可为空");

		DeleteResult result = mongoOperations.remove(idQuery(id), this.collectionName);
		return result.wasAcknowledged() && result.getDeletedCount() == 1;
	}

	public long removeByIds(Collection<String> ids) {
		List<String> validIds = CollectionUtils.emptyIfNull(ids)
			.stream()
			.filter(StringUtils::isNotBlank)
			.toList();
		if (validIds.isEmpty()) {
			return 0;
		}
		Query query = Query.query(Criteria.where(MongoConstants.ID_FIELD_NAME).in(validIds));
		return remove(query);
	}

	public boolean removeByObjectId(ObjectId objectId) {
		Assert.notNull(objectId, "objectId 不可为null");

		DeleteResult result = mongoOperations.remove(objectIdQuery(objectId), this.entityClass, this.collectionName);
		return result.wasAcknowledged() && result.getDeletedCount() == 1;
	}

	public long removeByObjectIds(Collection<ObjectId> objectIds) {
		List<String> validIds = CollectionUtils.emptyIfNull(objectIds)
			.stream()
			.filter(Objects::nonNull)
			.map(ObjectId::toHexString)
			.toList();
		if (validIds.isEmpty()) {
			return 0;
		}
		Query query = Query.query(Criteria.where(MongoConstants.ID_FIELD_NAME).in(validIds));
		return remove(query);
	}

	public long remove(Query query) {
		Assert.notNull(query, "query 不可为null");

		DeleteResult result = mongoOperations.remove(query, this.entityClass, this.collectionName);
		return result.wasAcknowledged() ? result.getDeletedCount() : 0;
	}

	protected Query objectIdQuery(ObjectId id) {
		return Query.query(objectIdCriteria(id));
	}

	protected Query idQuery(String id) {
		return Query.query(idCriteria(id));
	}

	public Criteria idCriteria(String id) {
		return Criteria.where(MongoConstants.ID_FIELD_NAME).is(id);
	}

	public Criteria objectIdCriteria(ObjectId id) {
		return Criteria.where(MongoConstants.ID_FIELD_NAME).is(id.toHexString());
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