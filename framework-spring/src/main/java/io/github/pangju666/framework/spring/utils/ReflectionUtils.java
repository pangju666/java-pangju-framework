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

import io.github.pangju666.framework.spring.lang.SpringConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.reflect.*;
import java.util.Objects;

/**
 * 反射操作工具类，继承并扩展了 {@link org.springframework.util.ReflectionUtils} 的功能。
 *
 * <p>封装了字段读取/写入（支持不可访问字段的 getter/setter 回退）、类信息提取、父类泛型解析、
 * 成员可访问性调整等常见反射操作。</p>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * // 读取/写入实例字段（不可访问时自动回退到 getter/setter）
 * User user = new User();
 * ReflectionUtils.setField(user, "name", "张三");
 * Object name = ReflectionUtils.getField(user, "name");
 *
 * // 按类型读取/写入字段
 * Integer age = ReflectionUtils.getField(user, "age", Integer.class);
 * ReflectionUtils.setField(user, "age", 30, Integer.class);
 *
 * // 解析父类泛型类型（支持默认第一个与指定索引）
 * class BaseRepo<T> {}
 * class UserRepo extends BaseRepo<User> {}
 * Class<?> generic0 = ReflectionUtils.getClassGenericType(UserRepo.class);    // => User.class
 * Class<?> genericN = ReflectionUtils.getClassGenericType(UserRepo.class, 0); // => User.class
 *
 * // 尝试设置成员可访问性（非 public 或 final）
 * Field f = User.class.getDeclaredField("name");
 * boolean fieldAccessible = ReflectionUtils.canMakeAccessible(f);  // 可能返回 true 并设置为可访问
 *
 * Method m = User.class.getDeclaredMethod("getName");
 * boolean methodAccessible = ReflectionUtils.canMakeAccessible(m); // 可能返回 true 并设置为可访问
 * }</pre>
 *
 * @author pangju666
 * @since 1.0.0
 * @see org.springframework.util.ReflectionUtils
 */
public class ReflectionUtils extends org.springframework.util.ReflectionUtils {
	protected ReflectionUtils() {
	}

	/**
	 * 获取实例字段值
	 *
	 * <p>
	 * 当字段可访问时，直接读取字段值；当字段不可直接访问时，尝试调用字段的 getter 方法并返回其结果。
	 * </p>
	 *
	 * @param target    目标实例（不可为 {@code null}）
	 * @param fieldName 字段名（不可为空或空白）
	 * @return 字段值，可能为 {@code null}
	 * @throws IllegalArgumentException 当 {@code target} 为 {@code null} 或 {@code fieldName} 为空/空白时抛出
	 * @throws IllegalStateException    当目标类中未找到对应字段、无法访问字段且不存在可访问的 getter 方法或反射调用失败时抛出
	 * @see #getField(Field, Object)
	 * @see #findField(Class, String)
	 * @see #findMethod(Class, String)
	 * @since 1.0.0
	 */
	@Nullable
	public static Object getField(final Object target, final String fieldName) {
		Assert.notNull(target, "target 不可为 null");
		Assert.hasText(fieldName, "fieldName 不可为空");

		Field field = findField(target.getClass(), fieldName);
		if (Objects.isNull(field)) {
			throw new IllegalStateException("字段：" + fieldName + " 未找到");
		}
		if (field.canAccess(target)) {
			return getField(field, target);
		}

		String methodName = SpringConstants.GETTER_PREFIX + StringUtils.capitalize(fieldName);
		Method method = findMethod(target.getClass(), methodName);
		if (Objects.isNull(method) || !method.canAccess(target)) {
			throw new IllegalStateException("无法访问字段：" + fieldName);
		}
		try {
			return method.invoke(target);
		} catch (Exception ex) {
			handleReflectionException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}

	/**
	 * 获取指定类型的实例字段值
	 *
	 * <p>
	 * 当字段可访问时，直接读取字段值；当字段不可直接访问时，尝试调用字段的 getter 方法并返回其结果。
	 * </p>
	 *
	 * @param target    目标实例（不可为 {@code null}）
	 * @param fieldName 字段名（不可为空或空白）
	 * @param type      字段类型（不可为 {@code null}）
	 * @param <T>       字段值类型
	 * @return 字段值，可能为 {@code null}
	 * @throws IllegalArgumentException 当 {@code target} 或 {@code type} 为 {@code null}，或 {@code fieldName} 为空/空白时抛出
	 * @throws IllegalStateException    当目标类中未找到对应字段，或字段不可访问且不存在可访问的 getter 方法，或反射调用失败时抛出
	 * @see #getField(Field, Object)
	 * @see #findField(Class, String, Class)
	 * @see #findMethod(Class, String)
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public static <T> T getField(final Object target, final String fieldName, final Class<T> type) {
		Assert.notNull(target, "target 不可为 null");
		Assert.notNull(type, "type 不可为 null");
		Assert.hasText(fieldName, "fieldName 不可为空");

		Field field = findField(target.getClass(), fieldName, type);
		if (Objects.isNull(field)) {
			throw new IllegalStateException("字段：" + fieldName + " 未找到");
		}
		if (field.canAccess(target)) {
			return (T) getField(field, target);
		}

		String methodName = SpringConstants.GETTER_PREFIX + StringUtils.capitalize(fieldName);
		Method method = findMethod(target.getClass(), methodName);
		if (Objects.isNull(method) || !method.canAccess(target)) {
			throw new IllegalStateException("无法访问字段：" + fieldName);
		}
		try {
			return (T) method.invoke(target);
		} catch (Exception ex) {
			handleReflectionException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}

	/**
	 * 设置实例字段值
	 *
	 * <p>
	 * 基于字段名设置实例字段值；当字段不可直接访问时，尝试调用字段的 setter 方法进行赋值。
	 * </p>
	 *
	 * @param target    目标实例（不可为 {@code null}）
	 * @param fieldName 字段名（不可为空或空白）
	 * @param value     字段值（可为 {@code null}）
	 * @throws IllegalArgumentException 当 {@code target} 为 {@code null} 或 {@code fieldName} 为空/空白时抛出
	 * @throws IllegalStateException    当目标类中未找到对应字段，或字段不可访问且不存在可访问的 setter 方法，或反射调用失败时抛出
	 * @see #setField(Object, String, Object, Class)
	 * @since 1.0.0
	 */
	public static void setField(final Object target, final String fieldName, @Nullable final Object value) {
		setField(target, fieldName, value, null);
	}

	/**
	 * 设置指定类型的实例字段值
	 *
	 * <p>
	 * 当字段可访问时，直接写入字段值；当字段不可直接访问时，尝试调用字段的 setter 方法进行赋值。
	 * </p>
	 *
	 * @param target    目标实例（不可为 {@code null}）
	 * @param fieldName 字段名（不可为空或空白）
	 * @param value     字段值（可为 {@code null}）
	 * @param type      字段类型（可为 {@code null}，为 {@code null} 时按名称匹配）
	 * @throws IllegalArgumentException 当 {@code target} 为 {@code null} 或 {@code fieldName} 为空/空白时抛出
	 * @throws IllegalStateException    当目标类中未找到对应字段，或字段不可访问且不存在可访问的 setter 方法，或反射调用失败时抛出
	 * @see #setField(Field, Object, Object)
	 * @see #findField(Class, String, Class)
	 * @see #findMethod(Class, String)
	 * @since 1.0.0
	 */
	public static <T> void setField(final Object target, final String fieldName, @Nullable final T value, @Nullable Class<T> type) {
		Assert.notNull(target, "target 不可为 null");
		Assert.hasText(fieldName, "fieldName 不可为空");

		Field field = findField(target.getClass(), fieldName, type);
		if (Objects.isNull(field)) {
			throw new IllegalStateException("字段：" + fieldName + " 未找到");
		}
		if (field.canAccess(target)) {
			setField(field, target, value);
		} else {
			String methodName = SpringConstants.SETTER_PREFIX + StringUtils.capitalize(fieldName);
			Method method = findMethod(target.getClass(), methodName);
			if (Objects.nonNull(method) && method.canAccess(target)) {
				try {
					method.invoke(target, value);
				} catch (Exception ex) {
					handleReflectionException(ex);
				}
			} else {
				throw new IllegalStateException("无法访问字段：" + fieldName);
			}
		}
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
	 * @param clazz 目标类对象，不能为空
	 * @param <T>   泛型类型
	 * @return 泛型参数对应的 {@link Class} 对象；无法确定类型时返回 {@code null}
	 * @see #getClassGenericType(Class, int)
	 * @see Class#getGenericSuperclass()
	 * @see ParameterizedType#getActualTypeArguments()
	 * @since 1.0.0
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
	 * @param clazz 目标类对象，不能为空
	 * @param index 泛型参数索引（从 0 开始）
	 * @param <T>   泛型类型
	 * @return 泛型参数对应的 {@link Class} 对象；无法确定类型时返回 {@code null}
	 * @see Class#getGenericSuperclass()
	 * @see ParameterizedType#getActualTypeArguments()
	 * @since 1.0.0
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
	 * @see #makeAccessible(Method)
	 * @since 1.0.0
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
