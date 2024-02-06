package org.rollbacker.core.cache;

import java.lang.reflect.Method;
import java.util.List;

public final class ExceptionerCache extends MethodCache<List<ExceptionerCache.FallthroughMethod>> {
    public static final ExceptionerCache instance = new ExceptionerCache();


    public record FallthroughMethod(boolean fallthrough, Method method) {
    }
}
