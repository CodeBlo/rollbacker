package org.rollbacker.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareError;

/**
 * Aspect for the exceptioner methods to notify the compiler to ensure only one argument which is a
 * subclass of throwable is given.
 */
@Aspect
public class ExceptionerAspect {
    @DeclareError("@annotation(org.rollbacker.annotation.Exceptioner) && !execution(* *(java.lang.Throwable+))")
    static final String ERROR_MESSAGE = "Exceptioner should only have 1 argument which argument class should be a subclass of throwable";
}
