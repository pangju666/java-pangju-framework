package io.github.pangju666.framework.core.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class BeanUtils {
	protected BeanUtils() {
	}

	public static <S, T> void copyProperties(final S source, final T target) {
		copyProperties(source, target, null);
	}

	public static <S, T> void copyProperties(final S source, final T target, final BiConsumer<S, T> consumer) {
		if (ObjectUtils.allNotNull(source, target)) {
			org.springframework.beans.BeanUtils.copyProperties(source, target);
			if (Objects.nonNull(consumer)) {
				consumer.accept(source, target);
			}
		}
	}

	public static <S, T> List<T> convertCollection(final Collection<S> source, final Converter<S, T> converter) {
		if (CollectionUtils.isEmpty(source)) {
			return Collections.emptyList();
		}
		return source.stream()
			.map(value -> convert(value, converter))
			.toList();
	}

	public static <S, T> T convert(final S source, final Converter<S, T> converter) {
		if (Objects.isNull(source)) {
			return null;
		}
		return converter.convert(source);
	}
}
