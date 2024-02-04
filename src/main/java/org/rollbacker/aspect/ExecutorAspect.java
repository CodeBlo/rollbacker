package org.rollbacker.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.rollbacker.annotation.Exceptioner;
import org.rollbacker.annotation.Executor;
import org.rollbacker.annotation.Rollbacker;
import org.slf4j.Logger;

import java.lang.reflect.Method;

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
        } catch (Throwable e) {

            Class<? extends Throwable>[] ignoredExceptions = executor.ignoredExceptions();
            for (Class<? extends Throwable> ignoredException : ignoredExceptions) {
                if (ignoredException.isAssignableFrom(e.getClass())) {
                    return null;
                }
            }
            String tag = executor.value();
            Object aThis = joinPoint.getThis();
            exceptioner(aThis, tag, e);
            log.warn("Error while running method {}. Rolling back...", joinPoint.toShortString());
            rollback(aThis, tag, joinPoint.toShortString());
        }
        return null;
    }

    private void exceptioner(Object aspectedObject, String tag, Throwable throwable) {
        Method[] declaredMethods = aspectedObject.getClass().getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            Exceptioner exceptioner = declaredMethod.getAnnotation(Exceptioner.class);
            if (exceptioner == null || !exceptioner.value().equals(tag)) {
                continue;
            }
            Class<?> parameterType = declaredMethod.getParameterTypes()[0];
            if (!parameterType.isAssignableFrom(throwable.getClass())) {
                continue;
            }
            declaredMethod.setAccessible(true);
            try {
                declaredMethod.invoke(aspectedObject, throwable);
            } catch (Exception ex) {
                log.error("Error while invoking exceptioner method", ex);
            }
            break;
        }
    }

    private void rollback(Object aspectedObject, String tag, String methodName) {
        Method[] declaredMethods = aspectedObject.getClass().getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            Rollbacker rollbacker = declaredMethod.getAnnotation(Rollbacker.class);
            if (rollbacker == null || !rollbacker.value().equals(tag)) {
                continue;
            }

            declaredMethod.setAccessible(true);
            try {
                declaredMethod.invoke(aspectedObject);
            } catch (Exception ex) {
                log.error("Error while executing rollback for method:{}", methodName);
            }
            break;
        }
    }


}
