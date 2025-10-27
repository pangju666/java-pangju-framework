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
 * 反射操作工具类，继承并扩展了 {@link org.springframework.util.ReflectionUtils} 的功能。
 *
 * <p>
 * 提供更加便捷的字段访问、方法调用、类信息提取、泛型类型解析等反射辅助操作，
 * 在 Spring 的基础工具之上进行了功能增强与异常包装处理。
 * </p>
 *
 * <p>
 * 设计灵感来自开源框架 <strong>RuoYi</strong>，并遵循线程安全、简洁可复用的原则。
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 * @see org.springframework.util.ReflectionUtils
 */
public class ReflectionUtils extends org.springframework.util.ReflectionUtils {
	protected ReflectionUtils() {
	}

	/**
	 * 获取指定对象中某字段的值。
	 *
	 * <p>该方法会递归查找父类中定义的字段。</p>
	 *
	 * @param obj       目标对象实例
	 * @param fieldName 字段名称
	 * @param <E>       返回值类型
	 * @return 字段的值；若字段不存在或访问失败则返回 {@code null}
	 * @since 1.0.0
	 * @see #getFieldValue(Object, Field)
	 */
	public static <E> E getFieldValue(final Object obj, final String fieldName) {
		Field field = findField(obj.getClass(), fieldName);
		if (Objects.isNull(field)) {
			return null;
		}
		return getFieldValue(obj, field);
	}

	/**
	 * 获取对象指定字段的值。
	 *
	 * <p>在访问私有字段时，会尝试修改可访问性。</p>
	 *
	 * @param obj   目标对象实例
	 * @param field 字段对象
	 * @param <E>   返回值类型
	 * @return 字段值；访问失败时返回 {@code null}
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
	 * 设置对象中指定字段的值。
	 *
	 * <p>该方法会递归查找字段定义于父类中的情况。</p>
	 *
	 * @param obj       目标对象实例
	 * @param fieldName 字段名称
	 * @param value     要设置的值
	 * @since 1.0.0
	 */
	public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
		Field field = findField(obj.getClass(), fieldName);
		if (Objects.isNull(field)) {
			return;
		}
		setFieldValue(obj, field, value);
	}

	/**
	 * 设置字段的值。
	 *
	 * <p>该方法会在必要时强制修改可访问性。</p>
	 *
	 * @param obj   目标对象实例
	 * @param field 字段对象
	 * @param value 要设置的值
	 * @since 1.0.0
	 */
	public static void setFieldValue(final Object obj, final Field field, final Object value) {
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
	 * 获取对象所属类的简单名称（不含包路径）。
	 *
	 * @param obj   对象实例
	 * @return 类的简单名称
	 * @since 1.0.0
	 */
	public static String getSimpleClassName(final Object obj) {
		return getSimpleClassName(obj.getClass());
	}

	/**
	 * 获取类的简单名称（不含包路径）。
	 *
	 * @param clz 类对象
	 * @return 简单类名
	 * @since 1.0.0
	 */
	public static String getSimpleClassName(final Class<?> clz) {
		return StringUtils.substringAfterLast(clz.getName(), ".");
	}

	/**
	 * 获取集合中元素的类型。
	 *
	 * <p>通过检查第一个元素的运行时类型进行判断。</p>
	 *
	 * @param collection 要分析的集合
	 * @param <T> 元素类型
	 * @return 集合元素的 {@link Class} 类型；集合为空时返回 {@code null}
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getCollectionElementType(final Collection<T> collection) {
		if (CollectionUtils.isEmpty(collection) || !collection.iterator().hasNext()) {
			return null;
		}
		return (Class<T>) collection.iterator().next().getClass();
	}

	/**
	 * 获取指定类的父类中定义的第一个泛型参数类型。
	 *
	 * <p>
	 * 本方法用于在运行时解析某个类的父类（superclass）所声明的泛型实参类型，
	 * 仅当父类定义了参数化类型（ParameterizedType）时有效。
	 * </p>
	 *
	 * <h3>使用示例：</h3>
	 * <pre>{@code
	 * class GenericParent<T> {}
	 * class MyClass extends GenericParent<String> {}
	 *
	 * Class<?> type = getClassGenericType(MyClass.class);
	 * // 返回 String.class
	 * }</pre>
	 *
	 * <h3>注意事项：</h3>
	 * <ul>
	 *   <li>仅支持获取“父类”定义的泛型类型，不支持接口或方法上的泛型。</li>
	 *   <li>当目标类未声明泛型参数、索引越界或类型擦除为非 Class 类型时，将返回 {@code null}。</li>
	 * </ul>
	 *
	 * @param clazz  目标类对象，不能为空
	 * @param <T>   泛型类型
	 * @return 泛型参数对应的 {@link Class} 对象；无法确定类型时返回 {@code null}
	 * @since 1.0.0
	 * @see Class#getGenericSuperclass()
	 * @see ParameterizedType#getActualTypeArguments()
	 */
	public static <T> Class<T> getClassGenericType(final Class<?> clazz) {
		return getClassGenericType(clazz, 0);
	}

	/**
	 * 获取指定类的父类中定义的泛型参数类型。
	 *
	 * <p>
	 * 本方法用于在运行时解析某个类的父类（superclass）所声明的泛型实参类型，
	 * 仅当父类定义了参数化类型（ParameterizedType）时有效。
	 * </p>
	 *
	 * <h3>使用示例：</h3>
	 * <pre>{@code
	 * class GenericParent<T> {}
	 * class MyClass extends GenericParent<String> {}
	 *
	 * Class<?> type = getClassGenericType(MyClass.class, 0);
	 * // 返回 String.class
	 * }</pre>
	 *
	 * <h3>注意事项：</h3>
	 * <ul>
	 *   <li>仅支持获取“父类”定义的泛型类型，不支持接口或方法上的泛型。</li>
	 *   <li>当目标类未声明泛型参数、索引越界或类型擦除为非 Class 类型时，将返回 {@code null}。</li>
	 * </ul>
	 *
	 * @param clazz  目标类对象，不能为空
	 * @param index  泛型参数索引（从 0 开始）
	 * @param <T>   泛型类型
	 * @return 泛型参数对应的 {@link Class} 对象；无法确定类型时返回 {@code null}
	 * @since 1.0.0
	 * @see Class#getGenericSuperclass()
	 * @see ParameterizedType#getActualTypeArguments()
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
	 * 尝试强制修改字段的可访问性。
	 *
	 * @param field 字段对象
	 * @return 成功设置可访问性时返回 {@code true}
	 * @see #makeAccessible(Field)
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
	 * 尝试强制修改方法的可访问性。
	 *
	 * @param method 方法对象
	 * @return 成功设置可访问性时返回 {@code true}
	 * @since 1.0.0
	 * @see #makeAccessible(Method)
	 */
	@SuppressWarnings("deprecation")
	public static boolean canMakeAccessible(final Method method) {
		if (!Modifier.isPublic(method.getModifiers()) ||
			!Modifier.isPublic(method.getDeclaringClass().getModifiers()) && !method.isAccessible()) {
			method.setAccessible(true);
			return true;
		}
		return false;
	}
}
