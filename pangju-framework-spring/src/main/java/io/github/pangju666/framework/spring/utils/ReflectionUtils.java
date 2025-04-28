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

package io.github.pangju666.framework.spring.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Objects;

/**
 * 反射操作工具类，继承并扩展了{@link org.springframework.util.ReflectionUtils}的功能
 * <p>提供字段访问、方法处理、类信息获取等反射相关操作</p>
 * <p>创意来自ruoyi</p>
 *
 * @author pangju666
 * @see org.springframework.util.ReflectionUtils
 * @since 1.0.0
 */
public class ReflectionUtils extends org.springframework.util.ReflectionUtils {
	protected ReflectionUtils() {
	}

	/**
	 * 获取对象指定字段的值
	 *
	 * @param obj       目标对象实例
	 * @param fieldName 要获取的字段名称
	 * @param <E>       返回值类型
	 * @return 字段值，若字段不存在返回null
	 * @since 1.0.0
	 */
	public static <E> E getFieldValue(final Object obj, final String fieldName) {
		Field field = getField(obj, fieldName);
		if (Objects.isNull(field)) {
			return null;
		}
		return getFieldValue(obj, field);
	}

	/**
	 * 通过反射字段对象获取字段值
	 *
	 * @param obj   目标对象实例
	 * @param field 要访问的字段对象
	 * @param <E>   返回值类型
	 * @return 字段值，若访问失败返回null
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	public static <E> E getFieldValue(final Object obj, final Field field) {
		boolean accessible = field.canAccess(obj);
		if (!accessible && !canMakeAccessible(field)) {
			return null;
		}
		try {
			E value = (E) field.get(obj);
			if (!accessible) {
				field.setAccessible(false);
			}
			return value;
		} catch (IllegalAccessException e) {
			ExceptionUtils.asRuntimeException(e);
			return null;
		}
	}

	/**
	 * 设置对象指定字段的值
	 *
	 * @param obj       目标对象实例
	 * @param fieldName 要设置的字段名称
	 * @param value     要设置的值
	 * @param <E>       值类型
	 * @since 1.0.0
	 */
	public static <E> void setFieldValue(final Object obj, final String fieldName, final E value) {
		Field field = getField(obj, fieldName);
		if (Objects.isNull(field)) {
			return;
		}
		setFieldValue(obj, field, value);
	}

	/**
	 * 通过反射字段对象设置字段值
	 *
	 * @param obj   目标对象实例
	 * @param field 要设置的字段对象
	 * @param value 要设置的值
	 * @param <E>   值类型
	 * @since 1.0.0
	 */
	public static <E> void setFieldValue(final Object obj, final Field field, final E value) {
		boolean accessible = field.canAccess(obj);
		if (!accessible && !canMakeAccessible(field)) {
			return;
		}
		try {
			field.set(obj, value);
			if (!accessible) {
				field.setAccessible(false);
			}
		} catch (IllegalAccessException e) {
			ExceptionUtils.asRuntimeException(e);
		}
	}

	/**
	 * 获取对象包含指定字段的Field对象
	 *
	 * @param obj       目标对象实例
	 * @param fieldName 要查找的字段名称
	 * @return 找到的字段对象，未找到返回null
	 * @since 1.0.0
	 */
	public static Field getField(final Object obj, final String fieldName) {
		if (Objects.isNull(obj)) {
			return null;
		}
		for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				return superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException ignored) {
			}
		}
		return null;
	}

	/**
	 * 获取对象的简化类名
	 *
	 * @param t   目标对象实例
	 * @param <T> 对象类型
	 * @return 类名（不包含包路径）
	 * @since 1.0.0
	 */
	public static <T> String getSimpleClassName(final T t) {
		return getSimpleClassName(t.getClass());
	}

	/**
	 * 获取类的简化名称
	 *
	 * @param clz 目标类对象
	 * @return 类名（不包含包路径）
	 * @since 1.0.0
	 */
	public static String getSimpleClassName(final Class<?> clz) {
		return StringUtils.substringAfterLast(clz.getName(), ".");
	}

	/**
	 * 获取集合中元素的类型。
	 * <p>
	 * 该方法通过检查集合中的第一个元素来确定集合中元素的类型。
	 * 如果集合为空或没有元素，则返回 null。
	 *
	 * @param collection 需要检查的集合
	 * @return 集合元素的类型，如果集合为空则返回 null
	 * @since 1.0.0
	 */
	public static Class<?> getCollectionElementType(final Collection<?> collection) {
		if (CollectionUtils.isEmpty(collection) || !collection.iterator().hasNext()) {
			return null;
		}
		return collection.iterator().next().getClass();
	}

	/**
	 * 获取类泛型的第一个类型参数
	 * <p>
	 * 此方法仅适用于获取父类定义了泛型参数，子类实现了该泛型参数的情况。
	 * </p>
	 * <p>
	 * 例如：{@code class MyClass extends GenericParent<String>} 可以获取到 {@code String.class}
	 * </p>
	 * <p>
	 * 注意：无法获取接口或方法上定义的泛型参数类型。
	 * </p>
	 *
	 * @param clazz 目标类对象
	 * @param <T>   泛型类型
	 * @return 泛型类型Class对象，无法获取时返回null
	 * @since 1.0.0
	 * @see Class#getGenericSuperclass()
	 */
	public static <T> Class<T> getClassGenericType(final Class<?> clazz) {
		return getClassGenericType(clazz, 0);
	}

	/**
	 * 获取指定索引的类泛型的类型参数
	 * <p>
	 * 此方法仅适用于获取父类定义了泛型参数，子类实现了该泛型参数的情况。
	 * </p>
	 * <p>
	 * 例如：{@code class MyClass extends GenericParent<String>} 可以获取到 {@code String.class}
	 * </p>
	 * <p>
	 * 注意：无法获取接口或方法上定义的泛型参数类型。
	 * </p>
	 *
	 * @param clazz 目标类对象
	 * @param <T>   泛型类型
	 * @return 泛型类型Class对象，无法获取时返回null
	 * @since 1.0.0
	 * @see Class#getGenericSuperclass()
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getClassGenericType(final Class<?> clazz, final int index) {
		Type genType = clazz.getGenericSuperclass();
		if (!(genType instanceof ParameterizedType)) {
			return null;
		}
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		if (index >= params.length || index < 0) {
			return null;
		}
		if (!(params[index] instanceof Class)) {
			return null;
		}
		return (Class<T>) params[index];
	}

	/**
	 * 强制设置字段可访问
	 *
	 * @param field 字段对象
	 * @return 当成功修改访问权限时返回true
	 * @since 1.0.0
	 */
	@SuppressWarnings("deprecation")
	public static boolean canMakeAccessible(final Field field) {
		if ((!Modifier.isPublic(field.getModifiers()) ||
			!Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
			Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
			field.setAccessible(true);
			return true;
		}
		return false;
	}

	/**
	 * 强制设置方法可访问
	 *
	 * @param method 方法对象
	 * @return 当成功修改访问权限时返回true
	 * @since 1.0.0
	 */
	@SuppressWarnings("deprecation")
	public static boolean canMakeAccessible(final Method method) {
		if ((!Modifier.isPublic(method.getModifiers()) ||
			!Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
			method.setAccessible(true);
			return true;
		}
		return false;
	}
}
