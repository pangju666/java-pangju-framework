package io.github.pangju666.framework.spring.utils.lang;

/**
 * 常用的一些常量
 *
 * @author pangju666
 * @since 1.0.0
 */
public class SpringConstants {
	/**
	 * set方法前缀
	 *
	 * @since 1.0.0
	 */
	public static final String SETTER_PREFIX = "set";
	/**
	 * get方法前缀
	 *
	 * @since 1.0.0
	 */
	public static final String GETTER_PREFIX = "get";
	/**
	 * cglib代理类前缀
	 *
	 * @since 1.0.0
	 */
	public static final String CGLIB_CLASS_SEPARATOR = "$$";
	/**
	 * cglib代理类方法前缀
	 *
	 * @since 1.0.0
	 */
	public static final String CGLIB_RENAMED_METHOD_PREFIX = "CGLIB$";
	protected SpringConstants() {
	}
}
