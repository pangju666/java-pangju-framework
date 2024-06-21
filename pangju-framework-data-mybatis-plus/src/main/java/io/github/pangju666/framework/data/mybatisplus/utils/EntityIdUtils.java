package io.github.pangju666.framework.data.mybatisplus.utils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class EntityIdUtils {
    protected EntityIdUtils() {
    }

    public static List<Long> getIdList(final Collection<Long> collection) {
        return collection.stream()
                .filter(id -> Objects.nonNull(id) && id >= 1)
                .toList();
    }

    public static List<Long> getUniqueIdList(final Collection<Long> collection) {
        return collection.stream()
                .filter(id -> Objects.nonNull(id) && id >= 1)
                .distinct()
                .toList();
    }
}