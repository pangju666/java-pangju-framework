package io.github.pangju666.framework.data.redis.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.util.*;
import java.util.stream.Collectors;

public class RedisUtils {
    // Redis相关常量
    public static final String REDIS_PATH_DELIMITER = "::";

    protected RedisUtils() {
    }

    public static String generateKey(final String... element) {
        return StringUtils.join(Arrays.asList(element), REDIS_PATH_DELIMITER);
    }

    public static <K> Set<K> likeRightKeys(final String keyword, final RedisTemplate<K, ?> redisTemplate) {
        try (var iterator = redisTemplate.scan(scanOptions(keyword + "*", null, null))) {
            return iterator.stream().collect(Collectors.toSet());
        }
    }

    public static <K> Set<K> likeRightKeys(final String keyword, final DataType dataType, final RedisTemplate<K, ?> redisTemplate) {
        try (var iterator = redisTemplate.scan(scanOptions(keyword + "*", dataType, null))) {
            return iterator.stream().collect(Collectors.toSet());
        }
    }

    public static <K> Set<K> likeLeftKeys(final String keyword, final RedisTemplate<K, ?> redisTemplate) {
        try (var iterator = redisTemplate.scan(scanOptions("*" + keyword, null, null))) {
            return iterator.stream().collect(Collectors.toSet());
        }
    }

    public static <K> Set<K> likeLeftKeys(final String keyword, final DataType dataType, final RedisTemplate<K, ?> redisTemplate) {
        try (var iterator = redisTemplate.scan(scanOptions("*" + keyword, dataType, null))) {
            return iterator.stream().collect(Collectors.toSet());
        }
    }

    public static <K> Set<K> likeKeys(final String keyword, final RedisTemplate<K, ?> redisTemplate) {
        try (var iterator = redisTemplate.scan(scanOptions("*" + keyword + "*", null, null))) {
            return iterator.stream().collect(Collectors.toSet());
        }
    }

    public static <K> Set<K> likeKeys(final String keyword, final DataType dataType, final RedisTemplate<K, ?> redisTemplate) {
        try (var iterator = redisTemplate.scan(scanOptions("*" + keyword + "*", dataType, null))) {
            return iterator.stream().collect(Collectors.toSet());
        }
    }

    public static <K> Set<K> keys(final RedisTemplate<K, ?> redisTemplate) {
        try (var iterator = redisTemplate.scan(scanOptions(null, null, null))) {
            return iterator.stream().collect(Collectors.toSet());
        }
    }

    public static <K> Set<K> keys(final String pattern, final RedisTemplate<K, ?> redisTemplate) {
        try (var iterator = redisTemplate.scan(scanOptions(pattern, null, null))) {
            return iterator.stream().collect(Collectors.toSet());
        }
    }

    public static <K> Set<K> keys(final DataType dataType, final RedisTemplate<K, ?> redisTemplate) {
        try (var iterator = redisTemplate.scan(scanOptions(null, dataType, null))) {
            return iterator.stream().collect(Collectors.toSet());
        }
    }

    public static <K> Set<K> keys(final String pattern, final DataType dataType, final RedisTemplate<K, ?> redisTemplate) {
        try (var iterator = redisTemplate.scan(scanOptions(pattern, dataType, null))) {
            return iterator.stream().collect(Collectors.toSet());
        }
    }

    public static <K, V> List<Pair<V, Double>> likeRightZSetValues(final K key, final String keyword, final RedisTemplate<K, V> redisTemplate) {
        try (var iterator = redisTemplate.opsForZSet().scan(key, scanOptions(keyword + "*", null, null))) {
            return iterator.stream()
                    .map(value -> Pair.of(value.getValue(), value.getScore()))
                    .toList();
        }
    }

    public static <K, V> List<Pair<V, Double>> likeLeftZSetValues(final K key, final String keyword, final RedisTemplate<K, V> redisTemplate) {
        try (var iterator = redisTemplate.opsForZSet().scan(key, scanOptions("*" + keyword, null, null))) {
            return iterator.stream()
                    .map(value -> Pair.of(value.getValue(), value.getScore()))
                    .toList();
        }
    }

    public static <K, V> List<Pair<V, Double>> likeZSetValues(final K key, final String keyword, final RedisTemplate<K, V> redisTemplate) {
        try (var iterator = redisTemplate.opsForZSet().scan(key, scanOptions("*" + keyword + "*", null, null))) {
            return iterator.stream()
                    .map(value -> Pair.of(value.getValue(), value.getScore()))
                    .toList();
        }
    }

    public static <K, V> List<Pair<V, Double>> zSetValues(final K key, final String pattern, final RedisTemplate<K, V> redisTemplate) {
        try (var iterator = redisTemplate.opsForZSet().scan(key, scanOptions(pattern, null, null))) {
            return iterator.stream()
                    .map(value -> Pair.of(value.getValue(), value.getScore()))
                    .toList();
        }
    }

    public static <K, V> List<Pair<V, Double>> zSetValues(final K key, final RedisTemplate<K, V> redisTemplate) {
        try (var iterator = redisTemplate.opsForZSet().scan(key, scanOptions(null, null, null))) {
            return iterator.stream()
                    .map(value -> Pair.of(value.getValue(), value.getScore()))
                    .toList();
        }
    }

    public static <K, V> Set<V> likeRightSetValues(final K key, final String keyword, final RedisTemplate<K, V> redisTemplate) {
        try (var iterator = redisTemplate.opsForSet().scan(key, scanOptions(keyword + "*", null, null))) {
            return iterator.stream()
                    .collect(Collectors.toSet());
        }
    }

    public static <K, V> Set<V> likeLeftSetValues(final K key, final String keyword, final RedisTemplate<K, V> redisTemplate) {
        try (var iterator = redisTemplate.opsForSet().scan(key, scanOptions("*" + keyword, null, null))) {
            return iterator.stream()
                    .collect(Collectors.toSet());
        }
    }

    public static <K, V> Set<V> likeSetValues(final K key, final String keyword, final RedisTemplate<K, V> redisTemplate) {
        try (var iterator = redisTemplate.opsForSet().scan(key, scanOptions("*" + keyword + "*", null, null))) {
            return iterator.stream()
                    .collect(Collectors.toSet());
        }
    }

    public static <K, V> Set<V> setValues(final K key, final RedisTemplate<K, V> redisTemplate) {
        try (var iterator = redisTemplate.opsForSet().scan(key, scanOptions(null, null, null))) {
            return iterator.stream()
                    .collect(Collectors.toSet());
        }
    }

    public static <K, V> Set<V> setValues(final K key, final String pattern, final RedisTemplate<K, V> redisTemplate) {
        try (var iterator = redisTemplate.opsForSet().scan(key, scanOptions(pattern, null, null))) {
            return iterator.stream()
                    .collect(Collectors.toSet());
        }
    }

    public static <K, HK, HV> Map<HK, HV> likeRightHashValues(final K key, final String keyword, final RedisTemplate<K, ?> redisTemplate) {
        HashOperations<K, HK, HV> hashOperations = redisTemplate.opsForHash();
        try (Cursor<Map.Entry<HK, HV>> iterator = hashOperations.scan(key, scanOptions(keyword + "*", null, null))) {
            return toMap(iterator.stream().toList());
        }
    }

    public static <K, HK, HV> Map<HK, HV> likeLeftHashValues(final K key, final String keyword, final RedisTemplate<K, ?> redisTemplate) {
        HashOperations<K, HK, HV> hashOperations = redisTemplate.opsForHash();
        try (var iterator = hashOperations.scan(key, scanOptions("*" + keyword, null, null))) {
            return toMap(iterator.stream().toList());
        }
    }

    public static <K, HK, HV> Map<HK, HV> likeHashValues(final K key, final String keyword, final RedisTemplate<K, ?> redisTemplate) {
        HashOperations<K, HK, HV> hashOperations = redisTemplate.opsForHash();
        try (var iterator = hashOperations.scan(key, scanOptions("*" + keyword + "*", null, null))) {
            return toMap(iterator.stream().toList());
        }
    }

    public static <K, HK, HV> Map<HK, HV> hashValues(final K key, final RedisTemplate<K, ?> redisTemplate) {
        HashOperations<K, HK, HV> hashOperations = redisTemplate.opsForHash();
        try (var iterator = hashOperations.scan(key, scanOptions(null, null, null))) {
            return toMap(iterator.stream().toList());
        }
    }

    public static <K, HK, HV> Map<HK, HV> hashValues(final K key, final String pattern, final RedisTemplate<K, ?> redisTemplate) {
        HashOperations<K, HK, HV> hashOperations = redisTemplate.opsForHash();
        try (var iterator = hashOperations.scan(key, scanOptions(pattern, null, null))) {
            return toMap(iterator.stream().toList());
        }
    }

    public static ScanOptions scanOptions(final String pattern, final DataType dataType, final Long count) {
        var builder = ScanOptions.scanOptions();
        if (Objects.nonNull(count)) {
            builder.count(count);
        }
        if (Objects.nonNull(dataType)) {
            builder.type(dataType);
        }
        if (StringUtils.isNotBlank(pattern)) {
            builder.match(pattern);
        }
        return builder.build();
    }

    public static <K> Long deleteKeys(final Collection<K> keys, final RedisTemplate<K, ?> redisTemplate, int retryTimes) {
        Long deleteCount = redisTemplate.delete(keys);
        if (Objects.isNull(deleteCount)) {
            return 0L;
        } else if (deleteCount < keys.size()) {
            long count = keys.size() - deleteCount;
            long times = 0;
            while (times <= retryTimes && count > 0) {
                Long tmpCount = redisTemplate.delete(keys);
                if (Objects.nonNull(tmpCount)) {
                    ++times;
                    count -= tmpCount;
                    deleteCount += tmpCount;
                }
            }
        }
        return deleteCount;
    }

    protected static <K, V> Map<K, V> toMap(final List<Map.Entry<K, V>> entries) {
        Map<K, V> map = new HashMap<>(entries.size());
        for (Map.Entry<K, V> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }
}
