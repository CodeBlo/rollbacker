package org.rollbacker.core;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public final class DefaultMethodProvider {

    public static final Method DEFAULT_ROLLBACKER = initDefaultRollbacker();

    private static Method initDefaultRollbacker() {
        try {
            Method defaultRollback = DefaultMethodProvider.class.getDeclaredMethod("defaultRollback");
            defaultRollback.setAccessible(true);
            return defaultRollback;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static void defaultRollback() {
        log.warn("No rollback added for executor. Using default rollbacker, which just logs this");
    }

}
