package org.rollbacker.core.cache;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
abstract class MethodCache<T> {

    private final Map<Key, T> methodCache = new ConcurrentHashMap<>();


    public final T computeIfAbsent(Class<?> clazz, String tag, BiFunction<Class<?>, String, T> biFunction) {
        Key key = new Key(clazz, tag);
        return methodCache.computeIfAbsent(key, (k) -> biFunction.apply(k.clazz(), k.tag()));
    }

    public final void add(Class<?> clazz, String tag, T value) {
        Key key = new Key(clazz, tag);
        methodCache.put(key, value);
    }


}
