package org.rollbacker.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareError;

@Aspect
public class ExceptionerAspect {
    @DeclareError("@annotation(org.rollbacker.annotation.Exceptioner) && !execution(* *(java.lang.Throwable+))")
    static final String ERROR_MESSAGE = "Exceptioner should only have 1 argument which argument class should be a subclass of throwable";
}
