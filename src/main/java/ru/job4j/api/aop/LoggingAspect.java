package ru.job4j.api.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* ru.job4j.api.services.*.*(..))")
    private void logMethodSignature() {

    }

    @Before("logMethodSignature()")
    public void logBeforeMethodInvocation(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] inputArguments = joinPoint.getArgs();

        LOG.info("Method '{}' has invoked with parameters: {}", methodName, inputArguments);
    }
}
