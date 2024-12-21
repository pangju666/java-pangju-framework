package io.github.pangju666.framework.data.mybatisplus.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.github.pangju666.commons.lang.utils.StreamUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AutoIDBasicDO implements Serializable {
	@TableId(type = IdType.AUTO)
	protected Long id;

	public static <T extends AutoIDBasicDO> List<Long> getIdList(Collection<T> entities) {
		return StreamUtils.toList(entities, T::getId);
	}

	public static <T extends AutoIDBasicDO> List<Long> getUniqueIdList(Collection<T> entities) {
		return StreamUtils.toUniqueList(entities, T::getId);
	}

	public static <T extends AutoIDBasicDO> Set<Long> getIdSet(Collection<T> entities) {
		return StreamUtils.toSet(entities, T::getId);
	}

	public static <T extends AutoIDBasicDO> Map<Long, T> mapById(Collection<T> entities) {
		return StreamUtils.toMap(entities, T::getId);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}