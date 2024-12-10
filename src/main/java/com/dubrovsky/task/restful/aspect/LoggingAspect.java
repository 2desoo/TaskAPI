package com.dubrovsky.task.restful.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("@annotation(LoggingExecution)")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Method {} is about to execute", joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "@annotation(LoggingException))", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        logger.error("Exception in method {}: {}", joinPoint.getSignature().getName(), exception.getMessage());
    }

    @AfterReturning(pointcut = "@annotation(LoggingReturn))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Method {} executed successfully {}", joinPoint.getSignature().getName(), result);
    }

    @Around("@annotation(LoggingAround)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable t) {
            logger.error("Error while executing method {}: {}", joinPoint.getSignature().getName(), t.getMessage());
            throw t;
        }
        logger.info("Method {} completed", joinPoint.getSignature().getName());
        return result;
    }

}
