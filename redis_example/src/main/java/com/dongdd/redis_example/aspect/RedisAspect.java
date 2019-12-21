package com.dongdd.redis_example.aspect;

import com.dongdd.redis_example.model.RedisModel;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

import java.util.Optional;

@Aspect
@Component
public class RedisAspect {

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Around(value = "execution(public * com.dongdd.redis_example.service.LongtimeService.getRedisModel(..))")
    public Mono<RedisModel> redisModelAspect(ProceedingJoinPoint proceedingJoinPoint) {
        String name = (String) proceedingJoinPoint.getArgs()[0];
        return CacheMono
                .lookup(k -> reactiveRedisTemplate.opsForValue()
                        .get(name)
                        .map(message -> RedisModel.builder().name(name).message(message).build())
                        .map(Signal::next), name)
                .onCacheMissResume(() -> {
                    try {
                        return (Mono<RedisModel>) proceedingJoinPoint.proceed();
                    } catch (Throwable throwable) {
                        return Mono.error(throwable);
                    }
                })
                .andWriteWith((k, sig) -> Mono.fromRunnable(() -> Optional.ofNullable((RedisModel) sig.get())
                        .filter(redisModel -> StringUtils.isNotEmpty(redisModel.getName()))
                        .ifPresent(redisModel -> reactiveRedisTemplate.opsForValue()
                                .set(redisModel.getName(), redisModel.getMessage()).subscribe())));

    }
}
