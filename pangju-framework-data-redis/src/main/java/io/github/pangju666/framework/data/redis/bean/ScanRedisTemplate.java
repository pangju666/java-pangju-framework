package io.github.pangju666.framework.data.redis.bean;

import io.github.pangju666.framework.data.redis.model.ZSetValue;
import io.github.pangju666.framework.data.redis.utils.RedisUtils;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * Redis扫描操作模板类
 * <p>
 * 该类扩展了Spring的RedisTemplate，提供了一系列基于SCAN命令的高效扫描操作。
 * 主要特点：
 * <ul>
 *     <li>支持键（Key）的渐进式扫描</li>
 *     <li>支持有序集合（ZSet）的渐进式扫描</li>
 *     <li>支持集合（Set）的渐进式扫描</li>
 *     <li>支持哈希表（Hash）的渐进式扫描</li>
 *     <li>所有操作都避免使用KEYS、SMEMBERS等全量命令，提供更好的性能</li>
 * </ul>
 * </p>
 *
 * <p>
 * 支持的匹配模式：
 * <ul>
 *     <li>前缀匹配：使用 "prefix*" 模式</li>
 *     <li>后缀匹配：使用 "*suffix" 模式</li>
 *     <li>关键字匹配：使用 "*keyword*" 模式</li>
 *     <li>自定义匹配：支持通过ScanOptions自定义匹配模式</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * @Autowired
 * private ScanRedisTemplate<String, Object> scanRedisTemplate;
 *
 * // 按前缀扫描键
 * Set<String> keys = scanRedisTemplate.scanKeysByPrefix("user:");
 *
 * // 按后缀扫描有序集合成员
 * SortedSet<ZSetValue<Object>> values = scanRedisTemplate.scanZSetValuesBySuffix("myset", ":score");
 *
 * // 按关键字扫描哈希表字段
 * Map<String, Object> fields = scanRedisTemplate.scanHashValuesByKeyword("myhash", "name");
 * }</pre>
 * </p>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @author pangju666
 * @see RedisTemplate
 * @see ScanOptions
 * @see RedisUtils
 * @since 1.0.0
 */
public class ScanRedisTemplate<K, V> extends RedisTemplate<K, V> {
	/**
	 * 构造一个新的 <code>ScanRedisTemplate</code> 实例。
	 * <p>{@link #setConnectionFactory(RedisConnectionFactory)} 和 {@link #afterPropertiesSet()} 仍需调用。</p>
	 *
	 * @since 1.0.0
	 */
	public ScanRedisTemplate() {
	}

	/**
	 * 构造一个新的 <code>ScanRedisTemplate</code> 实例以备使用。
	 *
	 * @param connectionFactory 用于创建新连接的连接工厂
	 * @since 1.0.0
	 */
	public ScanRedisTemplate(RedisConnectionFactory connectionFactory) {
		this();
		setConnectionFactory(connectionFactory);
		afterPropertiesSet();
	}

	/**
	 * 按后缀扫描键
	 * <p>
	 * 此方法使用SCAN命令进行渐进式扫描，避免使用KEYS命令可能带来的性能问题。
	 * 扫描所有以指定后缀结尾的键，例如：
	 * <ul>
	 *     <li>suffix="user" 将匹配 "app:user"、"system:user" 等</li>
	 *     <li>使用 "*suffix" 模式进行匹配</li>
	 *     <li>返回结果自动去重</li>
	 * </ul>
	 * </p>
	 *
	 * @param suffix 后缀字符串
	 * @return 匹配的键集合，如果suffix为空则返回空集合
	 * @since 1.0.0
	 */
	public Set<K> scanKeysBySuffix(final String suffix) {
		return RedisUtils.scanKeysBySuffix(suffix, this);
	}

	/**
	 * 按前缀扫描键
	 * <p>
	 * 此方法使用SCAN命令进行渐进式扫描，避免使用KEYS命令可能带来的性能问题。
	 * 扫描所有以指定前缀开头的键，例如：
	 * <ul>
	 *     <li>prefix="user" 将匹配 "user:1"、"user:profile" 等</li>
	 *     <li>使用 "prefix*" 模式进行匹配</li>
	 *     <li>返回结果自动去重</li>
	 * </ul>
	 * </p>
	 *
	 * @param prefix 前缀字符串
	 * @return 匹配的键集合，如果prefix为空则返回空集合
	 * @since 1.0.0
	 */
	public Set<K> scanKeysByPrefix(final String prefix) {
		return RedisUtils.scanKeysByPrefix(prefix, this);
	}

	/**
	 * 按关键字扫描键
	 * <p>
	 * 此方法使用SCAN命令进行渐进式扫描，避免使用KEYS命令可能带来的性能问题。
	 * 扫描所有包含指定关键字的键，例如：
	 * <ul>
	 *     <li>keyword="user" 将匹配 "app:user:1"、"system:user:profile" 等</li>
	 *     <li>使用 "*keyword*" 模式进行匹配</li>
	 *     <li>返回结果自动去重</li>
	 * </ul>
	 * </p>
	 *
	 * @param keyword 关键字
	 * @return 匹配的键集合，如果keyword为空则返回空集合
	 * @since 1.0.0
	 */
	public Set<K> scanKeysByKeyword(final String keyword) {
		return RedisUtils.scanKeysByKeyword(keyword, this);
	}

	/**
	 * 按数据类型扫描键
	 * <p>
	 * 扫描指定数据类型的所有键。此方法使用SCAN命令进行渐进式扫描，
	 * 避免使用KEYS命令可能带来的性能问题。
	 * </p>
	 *
	 * @param dataType 数据类型，支持的类型包括：STRING、LIST、SET、ZSET、HASH、STREAM
	 * @return 匹配数据类型的键集合，如果dataType为null则返回空集合
	 * @since 1.0.0
	 */
	public Set<K> scanKeysByDataType(final DataType dataType) {
		return RedisUtils.scanKeysByDataType(dataType, this);
	}

	/**
	 * 扫描所有键
	 * <p>
	 * 扫描Redis中的所有键，不进行任何过滤。此方法使用SCAN命令进行渐进式扫描，
	 * 避免使用KEYS命令可能带来的性能问题。
	 * </p>
	 *
	 * @return 所有键的集合
	 * @since 1.0.0
	 */
	public Set<K> scanKeys() {
		return RedisUtils.scanKeys(this);
	}

	/**
	 * 使用自定义扫描选项扫描键
	 * <p>
	 * 根据提供的扫描选项扫描Redis中的键。此方法使用SCAN命令进行渐进式扫描，
	 * 可以通过ScanOptions自定义匹配模式、数据类型和返回数量等选项。
	 * </p>
	 *
	 * @param scanOptions 扫描选项，可以指定匹配模式、数据类型和返回数量
	 * @return 匹配的键集合，如果scanOptions为null则返回空集合
	 * @see ScanOptions
	 * @see RedisUtils#scanOptions(String, DataType, Long)
	 * @since 1.0.0
	 */
	public Set<K> scanKeys(final ScanOptions scanOptions) {
		return RedisUtils.scanKeys(scanOptions, this);
	}

	/**
	 * 按后缀扫描有序集合成员
	 * <p>
	 * 此方法使用ZSCAN命令进行渐进式扫描，避免使用ZRANGE等命令可能带来的性能问题。
	 * 扫描所有以指定后缀结尾的成员，例如：
	 * <ul>
	 *     <li>suffix="score" 将匹配 "user:score"、"game:score" 等</li>
	 *     <li>使用 "*suffix" 模式进行匹配</li>
	 *     <li>返回结果按分数升序排序</li>
	 * </ul>
	 * </p>
	 *
	 * @param key    有序集合的键
	 * @param suffix 后缀字符串
	 * @return 匹配的成员及其分数的有序集合，如果suffix为空则返回空集合
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public SortedSet<ZSetValue<V>> scanZSetValuesBySuffix(final K key, final String suffix) {
		return RedisUtils.scanZSetValuesBySuffix(key, suffix, this);
	}

	/**
	 * 按前缀扫描有序集合成员
	 * <p>
	 * 此方法使用ZSCAN命令进行渐进式扫描，避免使用ZRANGE等命令可能带来的性能问题。
	 * 扫描所有以指定前缀开头的成员，例如：
	 * <ul>
	 *     <li>prefix="user" 将匹配 "user:1"、"user:score" 等</li>
	 *     <li>使用 "prefix*" 模式进行匹配</li>
	 *     <li>返回结果按分数升序排序</li>
	 * </ul>
	 * </p>
	 *
	 * @param key    有序集合的键
	 * @param prefix 前缀字符串
	 * @return 匹配的成员及其分数的有序集合，如果prefix为空则返回空集合
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public SortedSet<ZSetValue<V>> scanZSetValuesByPrefix(final K key, final String prefix) {
		return RedisUtils.scanZSetValuesByPrefix(key, prefix, this);
	}

	/**
	 * 按关键字扫描有序集合成员
	 * <p>
	 * 此方法使用ZSCAN命令进行渐进式扫描，避免使用ZRANGE等命令可能带来的性能问题。
	 * 扫描所有包含指定关键字的成员，例如：
	 * <ul>
	 *     <li>keyword="score" 将匹配 "high:score"、"user:score:100" 等</li>
	 *     <li>使用 "*keyword*" 模式进行匹配</li>
	 *     <li>返回结果按分数升序排序</li>
	 * </ul>
	 * </p>
	 *
	 * @param key     有序集合的键
	 * @param keyword 关键字
	 * @return 匹配的成员及其分数的有序集合，如果keyword为空则返回空集合
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public SortedSet<ZSetValue<V>> scanZSetValuesByKeyword(final K key, final String keyword) {
		return RedisUtils.scanZSetValuesByKeyword(key, keyword, this);
	}

	/**
	 * 扫描有序集合的所有成员
	 * <p>
	 * 此方法使用ZSCAN命令进行渐进式扫描，避免使用ZRANGE等命令可能带来的性能问题。
	 * 特点：
	 * <ul>
	 *     <li>不使用任何匹配模式，返回所有成员</li>
	 *     <li>返回结果按分数升序排序</li>
	 *     <li>支持大数据量的有序集合扫描</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 有序集合的键
	 * @return 所有成员及其分数的有序集合
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public SortedSet<ZSetValue<V>> scanZSetValues(final K key) {
		return RedisUtils.scanZSetValues(key, this);
	}

	/**
	 * 使用自定义扫描选项扫描有序集合成员
	 * <p>
	 * 此方法使用ZSCAN命令进行渐进式扫描，避免使用ZRANGE等命令可能带来的性能问题。
	 * 特点：
	 * <ul>
	 *     <li>支持自定义匹配模式</li>
	 *     <li>支持指定每次扫描返回的数量</li>
	 *     <li>返回结果按分数升序排序</li>
	 * </ul>
	 * </p>
	 *
	 * @param key         有序集合的键
	 * @param scanOptions 扫描选项，可以指定匹配模式和返回数量
	 * @return 匹配的成员及其分数的有序集合，如果scanOptions为null则返回空集合
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @see ScanOptions
	 * @see RedisUtils#scanOptions(String, DataType, Long)
	 * @since 1.0.0
	 */
	public SortedSet<ZSetValue<V>> scanZSetValues(final K key, final ScanOptions scanOptions) {
		return RedisUtils.scanZSetValues(key, scanOptions, this);
	}

	/**
	 * 按后缀扫描集合成员
	 * <p>
	 * 此方法使用SSCAN命令进行渐进式扫描，避免使用SMEMBERS命令可能带来的性能问题。
	 * 扫描所有以指定后缀结尾的成员，例如：
	 * <ul>
	 *     <li>suffix="user" 将匹配 "app:user"、"system:user" 等</li>
	 *     <li>使用 "*suffix" 模式进行匹配</li>
	 *     <li>返回结果无序且自动去重</li>
	 * </ul>
	 * </p>
	 *
	 * @param key    集合的键
	 * @param suffix 后缀字符串
	 * @return 匹配的成员集合，如果suffix为空则返回空集合
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public Set<V> scanSetValuesBySuffix(final K key, final String suffix) {
		return RedisUtils.scanSetValuesBySuffix(key, suffix, this);
	}

	/**
	 * 按前缀扫描集合成员
	 * <p>
	 * 此方法使用SSCAN命令进行渐进式扫描，避免使用SMEMBERS命令可能带来的性能问题。
	 * 扫描所有以指定前缀开头的成员，例如：
	 * <ul>
	 *     <li>prefix="user" 将匹配 "user:1"、"user:profile" 等</li>
	 *     <li>使用 "prefix*" 模式进行匹配</li>
	 *     <li>返回结果无序且自动去重</li>
	 * </ul>
	 * </p>
	 *
	 * @param key    集合的键
	 * @param prefix 前缀字符串
	 * @return 匹配的成员集合，如果prefix为空则返回空集合
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public Set<V> scanSetValuesByPrefix(final K key, final String prefix) {
		return RedisUtils.scanSetValuesByPrefix(key, prefix, this);
	}

	/**
	 * 按关键字扫描集合成员
	 * <p>
	 * 此方法使用SSCAN命令进行渐进式扫描，避免使用SMEMBERS命令可能带来的性能问题。
	 * 扫描所有包含指定关键字的成员，例如：
	 * <ul>
	 *     <li>keyword="user" 将匹配 "app:user:1"、"system:user:profile" 等</li>
	 *     <li>使用 "*keyword*" 模式进行匹配</li>
	 *     <li>返回结果无序且自动去重</li>
	 * </ul>
	 * </p>
	 *
	 * @param key     集合的键
	 * @param keyword 关键字
	 * @return 匹配的成员集合，如果keyword为空则返回空集合
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public Set<V> scanSetValuesByKeyword(final K key, final String keyword) {
		return RedisUtils.scanSetValuesByKeyword(key, keyword, this);
	}

	/**
	 * 扫描集合的所有成员
	 * <p>
	 * 此方法使用SSCAN命令进行渐进式扫描，避免使用SMEMBERS命令可能带来的性能问题。
	 * 特点：
	 * <ul>
	 *     <li>不使用任何匹配模式，返回所有成员</li>
	 *     <li>返回结果无序且自动去重</li>
	 *     <li>支持大数据量的集合扫描</li>
	 * </ul>
	 * </p>
	 *
	 * @param key 集合的键
	 * @return 集合中的所有成员
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public Set<V> scanSetValues(final K key) {
		return RedisUtils.scanSetValues(key, this);
	}

	/**
	 * 使用自定义扫描选项扫描集合成员
	 * <p>
	 * 此方法使用SSCAN命令进行渐进式扫描，避免使用SMEMBERS命令可能带来的性能问题。
	 * 特点：
	 * <ul>
	 *     <li>支持自定义匹配模式</li>
	 *     <li>支持指定每次扫描返回的数量</li>
	 *     <li>返回结果无序且自动去重</li>
	 *     <li>使用try-with-resources自动关闭游标</li>
	 * </ul>
	 * </p>
	 *
	 * @param key         集合的键
	 * @param scanOptions 扫描选项，可以指定匹配模式和返回数量
	 * @return 匹配的成员集合，如果scanOptions为null则返回空集合
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @see ScanOptions
	 * @see RedisUtils#scanOptions(String, DataType, Long)
	 * @since 1.0.0
	 */
	public Set<V> scanSetValues(final K key, final ScanOptions scanOptions) {
		return RedisUtils.scanSetValues(key, scanOptions, this);
	}

	/**
	 * 按后缀扫描哈希表字段
	 * <p>
	 * 此方法使用HSCAN命令进行渐进式扫描，避免使用HGETALL命令可能带来的性能问题。
	 * 扫描所有以指定后缀结尾的字段，例如：
	 * <ul>
	 *     <li>suffix="name" 将匹配 "first:name"、"last:name" 等</li>
	 *     <li>使用 "*suffix" 模式进行匹配</li>
	 *     <li>返回结果为字段和值的映射关系</li>
	 * </ul>
	 * </p>
	 *
	 * @param key    哈希表的键
	 * @param suffix 后缀字符串
	 * @param <HK>   哈希字段的类型
	 * @param <HV>   哈希值的类型
	 * @return 匹配的字段和值的映射，如果suffix为空则返回空映射
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public <HK, HV> Map<HK, HV> scanHashValuesBySuffix(final K key, final String suffix) {
		return RedisUtils.scanHashValuesBySuffix(key, suffix, this);
	}

	/**
	 * 按前缀扫描哈希表字段
	 * <p>
	 * 此方法使用HSCAN命令进行渐进式扫描，避免使用HGETALL命令可能带来的性能问题。
	 * 扫描所有以指定前缀开头的字段，例如：
	 * <ul>
	 *     <li>prefix="user" 将匹配 "user:id"、"user:name" 等</li>
	 *     <li>使用 "prefix*" 模式进行匹配</li>
	 *     <li>返回结果为字段和值的映射关系</li>
	 * </ul>
	 * </p>
	 *
	 * @param key    哈希表的键
	 * @param prefix 前缀字符串
	 * @param <HK>   哈希字段的类型
	 * @param <HV>   哈希值的类型
	 * @return 匹配的字段和值的映射，如果prefix为空则返回空映射
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public <HK, HV> Map<HK, HV> scanHashValuesByPrefix(final K key, final String prefix) {
		return RedisUtils.scanHashValuesByPrefix(key, prefix, this);
	}

	/**
	 * 按关键字扫描哈希表字段
	 * <p>
	 * 此方法使用HSCAN命令进行渐进式扫描，避免使用HGETALL命令可能带来的性能问题。
	 * 扫描所有包含指定关键字的字段，例如：
	 * <ul>
	 *     <li>keyword="name" 将匹配 "first:name"、"user:name:full" 等</li>
	 *     <li>使用 "*keyword*" 模式进行匹配</li>
	 *     <li>返回结果为字段和值的映射关系</li>
	 * </ul>
	 * </p>
	 *
	 * @param key     哈希表的键
	 * @param keyword 关键字
	 * @param <HK>    哈希字段的类型
	 * @param <HV>    哈希值的类型
	 * @return 匹配的字段和值的映射，如果keyword为空则返回空映射
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public <HK, HV> Map<HK, HV> scanHashValuesByKeyword(final K key, final String keyword) {
		return RedisUtils.scanHashValuesByKeyword(key, keyword, this);
	}

	/**
	 * 扫描哈希表的所有字段
	 * <p>
	 * 此方法使用HSCAN命令进行渐进式扫描，避免使用HGETALL命令可能带来的性能问题。
	 * 特点：
	 * <ul>
	 *     <li>不使用任何匹配模式，返回所有字段</li>
	 *     <li>返回结果为字段和值的映射关系</li>
	 *     <li>支持大数据量的哈希表扫描</li>
	 * </ul>
	 * </p>
	 *
	 * @param key  哈希表的键
	 * @param <HK> 哈希字段的类型
	 * @param <HV> 哈希值的类型
	 * @return 哈希表中的所有字段和值的映射
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @since 1.0.0
	 */
	public <HK, HV> Map<HK, HV> scanHashValues(final K key) {
		return RedisUtils.scanHashValues(key, this);
	}

	/**
	 * 使用自定义扫描选项扫描哈希表字段
	 * <p>
	 * 此方法使用HSCAN命令进行渐进式扫描，避免使用HGETALL命令可能带来的性能问题。
	 * 特点：
	 * <ul>
	 *     <li>支持自定义匹配模式</li>
	 *     <li>支持指定每次扫描返回的数量</li>
	 *     <li>返回结果为字段和值的映射关系</li>
	 *     <li>使用try-with-resources自动关闭游标</li>
	 * </ul>
	 * </p>
	 *
	 * @param key         哈希表的键
	 * @param scanOptions 扫描选项，可以指定匹配模式和返回数量
	 * @param <HK>        哈希字段的类型
	 * @param <HV>        哈希值的类型
	 * @return 匹配的字段和值的映射，如果scanOptions为null则返回空映射
	 * @throws IllegalArgumentException 当key为null时抛出
	 * @see ScanOptions
	 * @see RedisUtils#scanOptions(String, DataType, Long)
	 * @since 1.0.0
	 */
	public <HK, HV> Map<HK, HV> scanHashValues(final K key, final ScanOptions scanOptions) {
		return RedisUtils.scanHashValues(key, scanOptions, this);
	}
}