package io.github.pangju666.framework.data.mongo.repository;

import com.mongodb.client.result.DeleteResult;
import io.github.pangju666.commons.lang.utils.ReflectionUtils;
import io.github.pangju666.commons.lang.utils.StringUtils;
import io.github.pangju666.framework.data.mongo.model.BasicDocument;
import io.github.pangju666.framework.data.mongo.pool.MongoConstants;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public abstract class BaseRepository<T extends BasicDocument> {
	protected Class<T> entityClass;
	protected MongoOperations mongoOperations;
	protected String collectionName;

	protected BaseRepository(MongoOperations mongoOperations) {
		setMongoOperations(mongoOperations);
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
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

	public T getById(String id) {
		return mongoOperations.findById(id, this.entityClass, this.collectionName);
	}

	public Optional<T> getOptionalById(String id) {
		return Optional.ofNullable(mongoOperations.findById(id, this.entityClass, this.collectionName));
	}

	public T getByObjectId(ObjectId id) {
		return mongoOperations.findById(id, this.entityClass, this.collectionName);
	}

	public Optional<T> getOptionalByObjectId(ObjectId id) {
		return Optional.ofNullable(mongoOperations.findById(id, this.entityClass, this.collectionName));
	}

	public T getOne(Query query) {
		return mongoOperations.findOne(query, this.entityClass, this.collectionName);
	}

	public boolean existsById(String id) {
		return mongoOperations.exists(queryById(id), this.entityClass, this.collectionName);
	}

	public boolean existsByObjectId(ObjectId id) {
		return mongoOperations.exists(queryByObjectId(id), this.entityClass, this.collectionName);
	}

	public boolean exist(Query query) {
		return mongoOperations.exists(query, this.entityClass, this.collectionName);
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
		return mongoOperations.find(new Query().with(sort), this.entityClass, this.collectionName);
	}

	public List<T> list(Query query) {
		return mongoOperations.find(query, this.entityClass, this.collectionName);
	}

	public long count() {
		return mongoOperations.count(new Query(), this.collectionName);
	}

	public long count(Query query) {
		return mongoOperations.count(query, this.collectionName);
	}

	public Page<T> page(Pageable pageable) {
		long count = count();
		List<T> list = list(new Query().with(pageable));
		return new PageImpl<>(list, pageable, count);
	}

	public Page<T> page(Pageable pageable, Sort sort) {
		long count = count();
		List<T> list = list(new Query().with(pageable).with(sort));
		return new PageImpl<>(list, pageable, count);
	}

	public Page<T> page(Pageable pageable, Query query) {
		long count = count(query);
		List<T> list = list(query.with(pageable));
		return new PageImpl<>(list, pageable, count);
	}

	public Page<T> page(Pageable pageable, Query query, Sort sort) {
		long count = count(query);
		List<T> list = list(query.with(pageable).with(sort));
		return new PageImpl<>(list, pageable, count);
	}

	protected Query queryByObjectId(ObjectId id) {
		Criteria criteria = criteriaById(id.toHexString());
		return new Query(criteria);
	}

	protected Query queryById(String id) {
		Criteria criteria = criteriaById(id);
		return new Query(criteria);
	}

	public Query queryByIds(Collection<String> ids) {
		List<String> validIds = StringUtils.getUniqueNotBlankElements(ids);
		return new Query(new Criteria(MongoConstants.ID_FIELD_NAME).in(validIds));
	}

	public Query queryByObjectIds(Collection<ObjectId> ids) {
		Set<String> validIds = ids.stream()
			.filter(Objects::nonNull)
			.map(ObjectId::toHexString)
			.collect(Collectors.toSet());
		return new Query(new Criteria(MongoConstants.ID_FIELD_NAME).in(validIds));
	}

	protected Criteria criteriaById(String id) {
		return where(MongoConstants.ID_FIELD_NAME).is(id);
	}
}