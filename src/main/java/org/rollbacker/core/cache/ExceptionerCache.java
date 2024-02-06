package org.rollbacker.core.cache;

import java.lang.reflect.Method;
import java.util.List;

public final class ExceptionerCache extends MethodCache<List<ExceptionerCache.ExceptionerMethod>> {
    public static final ExceptionerCache instance = new ExceptionerCache();


    public record ExceptionerMethod(boolean fallthrough, Method method, Class<?> handles) {
    }
}
