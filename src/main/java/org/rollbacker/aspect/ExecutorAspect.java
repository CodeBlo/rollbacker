package org.rollbacker.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.rollbacker.annotation.Exceptioner;
import org.rollbacker.annotation.Executor;
import org.rollbacker.annotation.Rollbacker;
import org.rollbacker.core.DefaultMethodProvider;
import org.rollbacker.core.cache.ExceptionerCache;
import org.rollbacker.core.cache.RollbackerCache;
import org.rollbacker.core.util.CompareUtil;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Aspect for the executor methods.
 */
@Aspect
public class ExecutorAspect {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ExecutorAspect.class);

    /**
     * Finds the exceptioner and rollbacker methods for the executor method
     *
     * @param joinPoint join point
     * @param executor  annotation
     * @return what method returned or null if any exceptions occurred
     */
    @Around("@annotation(executor)")
    public Object around(ProceedingJoinPoint joinPoint, Executor executor) {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            Class<? extends Exception>[] ignoredExceptions = executor.ignoredExceptions();
            for (Class<? extends Exception> ignoredException : ignoredExceptions) {
                if (ignoredException.isAssignableFrom(e.getClass())) {
                    return null;
                }
            }
            String tag = executor.value();
            Object aThis = joinPoint.getThis();
            exceptioner(aThis, tag, e);
            log.warn("Error while running method {}. Rolling back...", joinPoint.toShortString());
            rollback(aThis, tag);
        } catch (Throwable e) {
            log.error("Unexpected error while running method {}", joinPoint.toShortString(), e);
        }
        return null;
    }

    private void exceptioner(Object aspectedObject, String tag, Exception exception) {
        ExceptionerCache cache = ExceptionerCache.instance;
        List<ExceptionerCache.ExceptionerMethod> methods = cache.computeIfAbsent(aspectedObject.getClass(), tag, this::getExceptionerMethods);

        for (ExceptionerCache.ExceptionerMethod exceptionerMethod : methods) {
            if (!exceptionerMethod.handles().isAssignableFrom(exception.getClass())) {
                continue;
            }

            try {
                exceptionerMethod.method().invoke(aspectedObject, exception);
            } catch (InvocationTargetException | IllegalAccessException e) {
                log.error("Error while invoking exceptioner method {}", exceptionerMethod, e);
            }
            if (!exceptionerMethod.fallthrough()) {
                break;
            }
        }

    }

    private List<ExceptionerCache.ExceptionerMethod> getExceptionerMethods(Class<?> clazz, String tag) {
        List<ExceptionerCache.ExceptionerMethod> exceptionerMethods = new ArrayList<>();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            Exceptioner exceptioner = declaredMethod.getAnnotation(Exceptioner.class);

            if (exceptioner == null || !exceptioner.value().equals(tag)) {
                continue;
            }

            declaredMethod.setAccessible(true);
            exceptionerMethods.add(new ExceptionerCache.ExceptionerMethod(exceptioner.fallthrough(), declaredMethod, declaredMethod.getParameterTypes()[0]));
        }
        exceptionerMethods.sort((m1, m2) -> CompareUtil.compareClassesByHierarchy(m1.handles(), m2.handles()));
        return exceptionerMethods;
    }

    private void rollback(Object aspectedObject, String tag) {
        RollbackerCache cache = RollbackerCache.instance;
        Method rollbacker = cache.computeIfAbsent(aspectedObject.getClass(), tag, this::getRollbackerMethod);

        try {
            rollbacker.invoke(aspectedObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Error while executing rollbacker {}", rollbacker, e);
        }
    }


    private Method getRollbackerMethod(Class<?> clazz, String tag) {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            Rollbacker rollbacker = declaredMethod.getAnnotation(Rollbacker.class);
            if (rollbacker == null || !rollbacker.value().equals(tag)) {
                continue;
            }

            declaredMethod.setAccessible(true);
            return declaredMethod;
        }
        return DefaultMethodProvider.DEFAULT_ROLLBACKER;
    }

}
