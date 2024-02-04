package org.rollbacker.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for marking a method as executor.
 * When an executor method fails, a similar tag with exceptioner and rollbacker will be executed
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Executor {
    String value() default "";

    Class<? extends Throwable>[] ignoredExceptions() default {};
}
