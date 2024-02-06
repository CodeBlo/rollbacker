package org.rollbacker.core.cache;

import java.lang.reflect.Method;

public final class RollbackerCache extends MethodCache<Method> {
    public static final RollbackerCache instance = new RollbackerCache();
}
