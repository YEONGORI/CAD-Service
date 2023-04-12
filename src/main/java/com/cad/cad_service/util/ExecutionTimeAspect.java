package com.cad.cad_service.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ExecutionTimeAspect {
    @Around("@annotation(measureExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, MeasureExecutionTime measureExecutionTime) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        System.out.println("{" + joinPoint.getSignature() + "}" + "executed in {" + (endTime - startTime) + "} ms");
        return proceed;
    }
}
