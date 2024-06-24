package io.github.pangju666.framework.data.redis.utils;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RedisUtils {
    protected static final int DELETE_RETRY_TIMES = 3;

    protected RedisUtils() {
    }

    public static String computeKey(final String... keys) {
        return StringUtils.join(Arrays.asList(keys), ConstantPool.REDIS_PATH_DELIMITER);
    }

    public static <K> Set<K> likeLeftKeys(final String keyword, final RedisTemplate<K, ?> redisTemplate) {
        try (var iterator = redisTemplate.scan(likeLeftScanOptions(keyword))) {
            return iterator.stream().collect(Collectors.toSet());
        }
    }

    public static <K> Set<K> likeRightKeys(final String keyword, final RedisTemplate<K, ?> redisTemplate) {
        try (var iterator = redisTemplate.scan(likeRightScanOptions(keyword))) {
            return iterator.stream().collect(Collectors.toSet());
        }
    }

    public static <K> Set<K> likeKeys(final String keyword, final RedisTemplate<K, ?> redisTemplate) {
        try (var iterator = redisTemplate.scan(likeScanOptions(keyword))) {
            return iterator.stream().collect(Collectors.toSet());
        }
    }

    public static <K> Set<K> keys(final RedisTemplate<K, ?> redisTemplate) {
        try (var iterator = redisTemplate.scan(emptyScanOptions())) {
            return iterator.stream().collect(Collectors.toSet());
        }
    }

    public static <K> Set<K> keys(final ScanOptions scanOptions, final RedisTemplate<K, ?> redisTemplate) {
        try (var iterator = redisTemplate.scan(scanOptions)) {
            return iterator.stream().collect(Collectors.toSet());
        }
    }

    public static <K, V> List<Pair<V, Double>> likeLeftZSetValues(final K key, final String keyword, final RedisTemplate<K, V> redisTemplate) {
        try (var iterator = redisTemplate.opsForZSet().scan(key, likeLeftScanOptions(keyword))) {
            return iterator.stream()
                    .map(value -> Pair.of(value.getValue(), value.getScore()))
                    .toList();
        }
    }

    public static <K, V> List<Pair<V, Double>> likeRightZSetValues(final K key, final String keyword, final RedisTemplate<K, V> redisTemplate) {
        try (var iterator = redisTemplate.opsForZSet().scan(key, likeRightScanOptions(keyword))) {
            return iterator.stream()
                    .map(value -> Pair.of(value.getValue(), value.getScore()))
                    .toList();
        }
    }

    public static <K, V> List<Pair<V, Double>> likeZSetValues(final K key, final String keyword, final RedisTemplate<K, V> redisTemplate) {
        try (var iterator = redisTemplate.opsForZSet().scan(key, likeScanOptions(keyword))) {
            return iterator.stream()
                    .map(value -> Pair.of(value.getValue(), value.getScore()))
                    .toList();
        }
    }

    public static <K, V> List<Pair<V, Double>> zSetValues(final K key, final RedisTemplate<K, V> redisTemplate) {
        try (var iterator = redisTemplate.opsForZSet().scan(key, emptyScanOptions())) {
            return iterator.stream()
                    .map(value -> Pair.of(value.getValue(), value.getScore()))
                    .toList();
        }
    }

    public static <K, V> List<Pair<V, Double>> zSetValues(final K key, final ScanOptions scanOptions, final RedisTemplate<K, V> redisTemplate) {
        try (var iterator = redisTemplate.opsForZSet().scan(key, scanOptions)) {
            return iterator.stream()
                    .map(value -> Pair.of(value.getValue(), value.getScore()))
                    .toList();
        }
    }

    public static <K, V> Set<V> likeLeftSetValues(final K key, final String keyword, final RedisTemplate<K, V> redisTemplate) {
        try (var iterator = redisTemplate.opsForSet().scan(key, likeLeftScanOptions(keyword))) {
            return iterator.stream()
                    .collect(Collectors.toSet());
        }
    }

    public static <K, V> Set<V> likeRightSetValues(final K key, final String keyword, final RedisTemplate<K, V> redisTemplate) {
        try (var iterator = redisTemplate.opsForSet().scan(key, likeRightScanOptions(keyword))) {
            return iterator.stream()
                    .collect(Collectors.toSet());
        }
    }

    public static <K, V> Set<V> likeSetValues(final K key, final String keyword, final RedisTemplate<K, V> redisTemplate) {
        try (var iterator = redisTemplate.opsForSet().scan(key, likeScanOptions(keyword))) {
            return iterator.stream()
                    .collect(Collectors.toSet());
        }
    }

    public static <K, V> Set<V> setValues(final K key, final RedisTemplate<K, V> redisTemplate) {
        try (var iterator = redisTemplate.opsForSet().scan(key, emptyScanOptions())) {
            return iterator.stream()
                    .collect(Collectors.toSet());
        }
    }

    public static <K, V> Set<V> setValues(final K key, final ScanOptions scanOptions, final RedisTemplate<K, V> redisTemplate) {
        try (var iterator = redisTemplate.opsForSet().scan(key, scanOptions)) {
            return iterator.stream()
                    .collect(Collectors.toSet());
        }
    }

    public static <K, HK, HV> Map<HK, HV> likeLeftHashValues(final K key, final String keyword, final RedisTemplate<K, ?> redisTemplate) {
        HashOperations<K, HK, HV> hashOperations = redisTemplate.opsForHash();
        try (var iterator = hashOperations.scan(key, likeLeftScanOptions(keyword))) {
            return toMap(iterator.stream().toList());
        }
    }

    public static <K, HK, HV> Map<HK, HV> likeRightHashValues(final K key, final String keyword, final RedisTemplate<K, ?> redisTemplate) {
        HashOperations<K, HK, HV> hashOperations = redisTemplate.opsForHash();
        try (Cursor<Map.Entry<HK, HV>> iterator = hashOperations.scan(key, likeRightScanOptions(keyword))) {
            return toMap(iterator.stream().toList());
        }
    }

    public static <K, HK, HV> Map<HK, HV> likeHashValues(final K key, final String keyword, final RedisTemplate<K, ?> redisTemplate) {
        HashOperations<K, HK, HV> hashOperations = redisTemplate.opsForHash();
        try (var iterator = hashOperations.scan(key, likeScanOptions(keyword))) {
            return toMap(iterator.stream().toList());
        }
    }

    public static <K, HK, HV> Map<HK, HV> hashValues(final K key, final RedisTemplate<K, ?> redisTemplate) {
        HashOperations<K, HK, HV> hashOperations = redisTemplate.opsForHash();
        try (var iterator = hashOperations.scan(key, emptyScanOptions())) {
            return toMap(iterator.stream().toList());
        }
    }

    public static <K, HK, HV> Map<HK, HV> hashValues(final K key, final ScanOptions scanOptions, final RedisTemplate<K, ?> redisTemplate) {
        HashOperations<K, HK, HV> hashOperations = redisTemplate.opsForHash();
        try (var iterator = hashOperations.scan(key, scanOptions)) {
            return toMap(iterator.stream().toList());
        }
    }

    public static ScanOptions likeLeftScanOptions(final String keyword) {
        Assert.hasText(keyword, "keyword不可为空");
        return scanOptions("*" + keyword, null, null);
    }

    public static ScanOptions likeLeftScanOptions(final String keyword, @Nullable final DataType dataType, @Nullable final Long count) {
        Assert.hasText(keyword, "keyword不可为空");
        return scanOptions("*" + keyword, dataType, count);
    }

    public static ScanOptions likeRightScanOptions(final String keyword) {
        Assert.hasText(keyword, "keyword不可为空");
        return scanOptions(keyword + "*", null, null);
    }

    public static ScanOptions likeRightScanOptions(final String keyword, @Nullable final DataType dataType, @Nullable final Long count) {
        Assert.hasText(keyword, "keyword不可为空");
        return scanOptions(keyword + "*", dataType, count);
    }

    public static ScanOptions likeScanOptions(final String keyword) {
        Assert.hasText(keyword, "keyword不可为空");
        return scanOptions("*" + keyword + "*", null, null);
    }

    public static ScanOptions likeScanOptions(final String keyword, @Nullable final DataType dataType, @Nullable final Long count) {
        Assert.hasText(keyword, "keyword不可为空");
        return scanOptions("*" + keyword + "*", dataType, count);
    }

    public static ScanOptions emptyScanOptions() {
        return scanOptions(null, null, null);
    }

    public static ScanOptions scanOptions(@Nullable final String pattern, @Nullable final DataType dataType, @Nullable final Long count) {
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

    public static <K> Long delete(final Collection<K> keys, final RedisTemplate<K, ?> redisTemplate,
                                  final Function<RedisTemplate<K, ?>, Long> function) {
        return delete(keys, redisTemplate, function, DELETE_RETRY_TIMES);
    }

    public static <K> Long delete(final Collection<K> keys, final RedisTemplate<K, ?> redisTemplate,
                                  final Function<RedisTemplate<K, ?>, Long> function, int retryTimes) {
        if (CollectionUtils.isEmpty(keys)) {
            return 0L;
        }
        Long deleteCount = function.apply(redisTemplate);
        if (Objects.isNull(deleteCount)) {
            return 0L;
        } else if (deleteCount < keys.size()) {
            long count = keys.size() - deleteCount;
            long times = 0;
            while (times <= retryTimes && count > 0) {
                Long tmpCount = function.apply(redisTemplate);
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