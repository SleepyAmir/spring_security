package com.mftplus.spring_security.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    
    @Pointcut("execution(* com.mftplus.spring_security.core..service.*.*(..))")
    public void serviceMethods() {}
    
    @Pointcut("execution(* com.mftplus.spring_security..controller.*.*(..))")
    public void controllerMethods() {}
    
    @Before("serviceMethods() || controllerMethods()")
    public void logMethodEntry(JoinPoint joinPoint) {
        String username = getCurrentUsername();
        log.info("Method Entry: {} called by user: {} with arguments: {}", 
                joinPoint.getSignature().toShortString(), 
                username,
                Arrays.toString(joinPoint.getArgs()));
    }
    
    @AfterReturning(pointcut = "serviceMethods() || controllerMethods()", returning = "result")
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        String username = getCurrentUsername();
        log.info("Method Exit: {} called by user: {} returned: {}", 
                joinPoint.getSignature().toShortString(), 
                username,
                result != null ? result.getClass().getSimpleName() : "null");
    }
    
    @AfterThrowing(pointcut = "serviceMethods() || controllerMethods()", throwing = "exception")
    public void logMethodException(JoinPoint joinPoint, Exception exception) {
        String username = getCurrentUsername();
        log.error("Exception in method: {} called by user: {} - Exception: {}", 
                joinPoint.getSignature().toShortString(), 
                username,
                exception.getMessage(), 
                exception);
    }
    
    @Around("serviceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            log.debug("Method {} executed in {} ms", 
                    joinPoint.getSignature().toShortString(), 
                    executionTime);
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Method {} failed after {} ms", 
                    joinPoint.getSignature().toShortString(), 
                    executionTime);
            throw e;
        }
    }
    
    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getName().equals("anonymousUser")) {
                return authentication.getName();
            }
        } catch (Exception e) {
            log.debug("Could not get username from security context", e);
        }
        return "anonymous";
    }
}

