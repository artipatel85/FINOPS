package com.finops.aop;

import ch.qos.logback.classic.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class MeasureTimeAdvice {

    Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    @Around("@annotation(com.finops.aop.MeasureTime)")
    public Object measureTime(ProceedingJoinPoint point) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object object = point.proceed();
        stopWatch.stop();
        logger.debug("******* Time taken by {} method is {} ms",
                point.getSignature().getName() + "()", stopWatch.getTotalTimeMillis());
        return object;
    }
}
