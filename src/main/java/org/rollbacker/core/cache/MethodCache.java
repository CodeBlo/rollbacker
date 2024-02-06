package org.rollbacker.core.cache;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
abstract class MethodCache<T> {

    private final Map<Key, T> methodCache = new HashMap<>();

    final T get(Class<?> clazz, String tag) {
        return methodCache.get(new Key(clazz, tag));
    }

    void add(Class<?> clazz, String tag, T value) {
        Key key = new Key(clazz, tag);
        methodCache.put(key, value);
    }


}
