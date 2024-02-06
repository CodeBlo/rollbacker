package org.rollbacker.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for marking a method as exceptioner. Exceptioner methods will handle specific exceptions to operate
 * on a specific executor method with the given tag.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Exceptioner {
    String value() default "";

    boolean fallthrough() default false;
}
