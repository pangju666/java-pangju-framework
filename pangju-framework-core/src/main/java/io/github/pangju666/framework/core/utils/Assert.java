package io.github.pangju666.framework.core.utils;

import io.github.pangju666.framework.core.exception.base.ValidationException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Assert {
	protected Assert() {
	}

	public static void isTrue(boolean expression, final String message) {
		if (!expression) {
			throw new ValidationException(message);
		}
	}

	public static void isTrue(boolean expression, final Supplier<String> messageSupplier) {
		if (!expression) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static void isNull(final Object object, final String message) {
		if (object != null) {
			throw new ValidationException(message);
		}
	}

	public static void isNull(final Object object, final Supplier<String> messageSupplier) {
		if (object != null) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static void notNull(final Object object, final String message) {
		if (object == null) {
			throw new ValidationException(message);
		}
	}

	public static void notNull(final Object object, final Supplier<String> messageSupplier) {
		if (object == null) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static <T> void equals(final T object, final T target, final String message) {
		if (!Objects.equals(object, target)) {
			throw new ValidationException(message);
		}
	}

	public static <T> void equals(final T object, final T target, final Supplier<String> messageSupplier) {
		if (!Objects.equals(object, target)) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static <T> void notEquals(final T object, final T target, final String message) {
		if (Objects.equals(object, target)) {
			throw new ValidationException(message);
		}
	}

	public static <T> void notEquals(final T object, final T target, final Supplier<String> messageSupplier) {
		if (Objects.equals(object, target)) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static <T> void match(final T object, final Predicate<T> predicate, final String message) {
		if (!predicate.test(object)) {
			throw new ValidationException(message);
		}
	}

	public static <T> void match(final T object, final Predicate<T> predicate, final Supplier<String> messageSupplier) {
		if (!predicate.test(object)) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static <T> void notMatch(final T object, final Predicate<T> predicate, final String message) {
		if (predicate.test(object)) {
			throw new ValidationException(message);
		}
	}

	public static <T> void notMatch(final T object, final Predicate<T> predicate, final Supplier<String> messageSupplier) {
		if (predicate.test(object)) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static void notEmpty(final String text, final String message) {
		if (StringUtils.isEmpty(text)) {
			throw new ValidationException(message);
		}
	}

	public static void notEmpty(final String text, final Supplier<String> messageSupplier) {
		if (StringUtils.isEmpty(text)) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static void notBlank(final String text, final String message) {
		if (StringUtils.isBlank(text)) {
			throw new ValidationException(message);
		}
	}

	public static void notBlank(final String text, final Supplier<String> messageSupplier) {
		if (!StringUtils.isBlank(text)) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static void contain(final String textToSearch, final String substring, final String message) {
		if (!StringUtils.contains(substring, textToSearch)) {
			throw new ValidationException(message);
		}
	}

	public static void contain(final String textToSearch, final String substring, final Supplier<String> messageSupplier) {
		if (!StringUtils.contains(substring, textToSearch)) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static void notContain(final String textToSearch, final String substring, final String message) {
		if (StringUtils.contains(substring, textToSearch)) {
			throw new ValidationException(message);
		}
	}

	public static void notContain(final String textToSearch, final String substring, final Supplier<String> messageSupplier) {
		if (StringUtils.contains(substring, textToSearch)) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static void match(final String textToSearch, final Predicate<String> predicate, final String message) {
		if (!predicate.test(textToSearch)) {
			throw new ValidationException(message);
		}
	}

	public static void match(final String textToSearch, final Predicate<String> predicate, final Supplier<String> messageSupplier) {
		if (!predicate.test(textToSearch)) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static void notMatch(final String textToSearch, final Predicate<String> predicate, final String message) {
		if (predicate.test(textToSearch)) {
			throw new ValidationException(message);
		}
	}

	public static void notMatch(final String textToSearch, final Predicate<String> predicate, final Supplier<String> messageSupplier) {
		if (predicate.test(textToSearch)) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static void notEmpty(final Object[] array, final String message) {
		if (ArrayUtils.isEmpty(array)) {
			throw new ValidationException(message);
		}
	}

	public static void notEmpty(final Object[] array, final Supplier<String> messageSupplier) {
		if (ArrayUtils.isEmpty(array)) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static void noNullElements(final Object[] array, final String message) {
		if (array != null) {
			for (Object element : array) {
				if (element == null) {
					throw new ValidationException(message);
				}
			}
		}
	}

	public static void noNullElements(final Object[] array, final Supplier<String> messageSupplier) {
		if (array != null) {
			for (Object element : array) {
				if (element == null) {
					throw new ValidationException(messageSupplier.get());
				}
			}
		}
	}

	public static <T> void notContain(final T[] array, final T target, final String message) {
		if (ArrayUtils.contains(array, target)) {
			throw new ValidationException(message);
		}
	}

	public static <T> void notContain(final T[] array, final T target, final Supplier<String> messageSupplier) {
		if (ArrayUtils.contains(array, target)) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static <T> void contain(final T[] array, final T target, final String message) {
		if (!ArrayUtils.contains(array, target)) {
			throw new ValidationException(message);
		}
	}

	public static <T> void contain(final T[] array, final T target, final Supplier<String> messageSupplier) {
		if (!ArrayUtils.contains(array, target)) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static <T> void noneMatch(final T[] array, final Predicate<T> predicate, final String message) {
		if (ArrayUtils.isNotEmpty(array) && Arrays.stream(array).anyMatch(predicate)) {
			throw new ValidationException(message);
		}
	}

	public static <T> void noneMatch(final T[] array, final Predicate<T> predicate, final Supplier<String> messageSupplier) {
		if (ArrayUtils.isNotEmpty(array) && Arrays.stream(array).anyMatch(predicate)) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static <T> void allMatch(final T[] array, final Predicate<T> predicate, final String message) {
		if (ArrayUtils.isNotEmpty(array) && !Arrays.stream(array).allMatch(predicate)) {
			throw new ValidationException(message);
		}
	}

	public static <T> void allMatch(final T[] array, final Predicate<T> predicate, final Supplier<String> messageSupplier) {
		if (ArrayUtils.isNotEmpty(array) && !Arrays.stream(array).allMatch(predicate)) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static <T> void anyMatch(final T[] array, final Predicate<T> predicate, final String message) {
		if (ArrayUtils.isNotEmpty(array) && Arrays.stream(array).noneMatch(predicate)) {
			throw new ValidationException(message);
		}
	}

	public static <T> void anyMatch(final T[] array, final Predicate<T> predicate, final Supplier<String> messageSupplier) {
		if (array != null && Arrays.stream(array).noneMatch(predicate)) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static void notEmpty(final Collection<?> collection, final String message) {
		if (CollectionUtils.isEmpty(collection)) {
			throw new ValidationException(message);
		}
	}

	public static void notEmpty(final Collection<?> collection, final Supplier<String> messageSupplier) {
		if (CollectionUtils.isEmpty(collection)) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static void noNullElements(final Collection<?> collection, final String message) {
		if (collection != null) {
			for (Object element : collection) {
				if (element == null) {
					throw new ValidationException(message);
				}
			}
		}
	}

	public static void noNullElements(final Collection<?> collection, final Supplier<String> messageSupplier) {
		if (collection != null) {
			for (Object element : collection) {
				if (element == null) {
					throw new ValidationException(messageSupplier.get());
				}
			}
		}
	}

	public static <T> void notContain(final Collection<T> collection, final T target, final String message) {
		if (CollectionUtils.isNotEmpty(collection)) {
			for (T element : collection) {
				if (Objects.equals(element, target)) {
					throw new ValidationException(message);
				}
			}
		}
	}

	public static <T> void notContain(final Collection<T> collection, final T target, final Supplier<String> messageSupplier) {
		if (CollectionUtils.isNotEmpty(collection)) {
			for (T element : collection) {
				if (Objects.equals(element, target)) {
					throw new ValidationException(messageSupplier.get());
				}
			}
		}
	}

	public static <T> void contain(final Collection<T> collection, final T target, final String message) {
		if (CollectionUtils.isNotEmpty(collection)) {
			for (T element : collection) {
				if (!Objects.equals(element, target)) {
					throw new ValidationException(message);
				}
			}
		}
	}

	public static <T> void contain(final Collection<T> collection, final T target, final Supplier<String> messageSupplier) {
		if (CollectionUtils.isNotEmpty(collection)) {
			for (T element : collection) {
				if (!Objects.equals(element, target)) {
					throw new ValidationException(messageSupplier.get());
				}
			}
		}
	}

	public static <T> void noneMatch(final Collection<T> collection, final Predicate<T> predicate, final String message) {
		if (CollectionUtils.isNotEmpty(collection) && collection.stream().anyMatch(predicate)) {
			throw new ValidationException(message);
		}
	}

	public static <T> void noneMatch(final Collection<T> collection, final Predicate<T> predicate, final Supplier<String> messageSupplier) {
		if (CollectionUtils.isNotEmpty(collection) && collection.stream().anyMatch(predicate)) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static <T> void allMatch(final Collection<T> collection, final Predicate<T> predicate, final String message) {
		if (CollectionUtils.isNotEmpty(collection) && !collection.stream().allMatch(predicate)) {
			throw new ValidationException(message);
		}
	}

	public static <T> void allMatch(final Collection<T> collection, final Predicate<T> predicate, final Supplier<String> messageSupplier) {
		if (CollectionUtils.isNotEmpty(collection) && !collection.stream().allMatch(predicate)) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static <T> void anyMatch(final Collection<T> collection, final Predicate<T> predicate, final String message) {
		if (CollectionUtils.isNotEmpty(collection) && collection.stream().noneMatch(predicate)) {
			throw new ValidationException(message);
		}
	}

	public static <T> void anyMatch(final Collection<T> collection, final Predicate<T> predicate, final Supplier<String> messageSupplier) {
		if (CollectionUtils.isNotEmpty(collection) && collection.stream().noneMatch(predicate)) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static void notEmpty(final Map<?, ?> map, final String message) {
		if (MapUtils.isEmpty(map)) {
			throw new ValidationException(message);
		}
	}

	public static void notEmpty(final Map<?, ?> map, final Supplier<String> messageSupplier) {
		if (MapUtils.isEmpty(map)) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static <K> void hasKey(final Map<K, ?> map, final K key, final String message) {
		if (MapUtils.isNotEmpty(map) && (map.containsKey(key))) {
			throw new ValidationException(message);
		}
	}

	public static <K> void hasKey(final Map<K, ?> map, final K key, final Supplier<String> messageSupplier) {
		if (MapUtils.isNotEmpty(map) && (map.containsKey(key))) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static <V> void hasValue(final Map<?, V> map, final V value, final String message) {
		if (MapUtils.isNotEmpty(map) && (map.containsValue(value))) {
			throw new ValidationException(message);
		}
	}

	public static <V> void hasValue(final Map<?, V> map, final V value, final Supplier<String> messageSupplier) {
		if (MapUtils.isNotEmpty(map) && (map.containsValue(value))) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static <K, V> void noneMatch(final Map<K, V> map, final BiPredicate<K, V> predicate, final String message) {
		if (MapUtils.isNotEmpty(map) && map.entrySet().stream().anyMatch(entry -> predicate.test(entry.getKey(), entry.getValue()))) {
			throw new ValidationException(message);
		}
	}

	public static <K, V> void noneMatch(final Map<K, V> map, final BiPredicate<K, V> predicate, final Supplier<String> messageSupplier) {
		if (MapUtils.isNotEmpty(map) && map.entrySet().stream().anyMatch(entry -> predicate.test(entry.getKey(), entry.getValue()))) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static <K, V> void allMatch(final Map<K, V> map, final BiPredicate<K, V> predicate, final String message) {
		if (MapUtils.isNotEmpty(map) && !map.entrySet().stream().allMatch(entry -> predicate.test(entry.getKey(), entry.getValue()))) {
			throw new ValidationException(message);
		}
	}

	public static <K, V> void allMatch(final Map<K, V> map, final BiPredicate<K, V> predicate, final Supplier<String> messageSupplier) {
		if (MapUtils.isNotEmpty(map) && !map.entrySet().stream().allMatch(entry -> predicate.test(entry.getKey(), entry.getValue()))) {
			throw new ValidationException(messageSupplier.get());
		}
	}

	public static <K, V> void anyMatch(final Map<K, V> map, final BiPredicate<K, V> predicate, final String message) {
		if (MapUtils.isNotEmpty(map) && map.entrySet().stream().noneMatch(entry -> predicate.test(entry.getKey(), entry.getValue()))) {
			throw new ValidationException(message);
		}
	}

	public static <K, V> void anyMatch(final Map<K, V> map, final BiPredicate<K, V> predicate, final Supplier<String> messageSupplier) {
		if (MapUtils.isNotEmpty(map) && map.entrySet().stream().noneMatch(entry -> predicate.test(entry.getKey(), entry.getValue()))) {
			throw new ValidationException(messageSupplier.get());
		}
	}
}
