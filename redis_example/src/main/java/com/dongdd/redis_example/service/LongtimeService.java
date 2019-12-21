package com.dongdd.redis_example.service;

import com.dongdd.redis_example.model.RedisModel;
import reactor.core.publisher.Mono;

public interface LongtimeService {

    Mono<RedisModel> getRedisModel(String name);
    Mono<Void> putRedisModel(RedisModel redisModel);
}
