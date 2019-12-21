package com.dongdd.redis_example.service.impl;

import com.dongdd.redis_example.model.RedisModel;
import com.dongdd.redis_example.service.LongtimeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LongtimeServiceImpl implements LongtimeService {

    private List<RedisModel> redisModelList = new ArrayList<>();

    public Mono<RedisModel> getRedisModel(String name) {
        return Mono.just(redisModelList)
                .map(redisModels -> redisModels.stream().filter(redisModel -> redisModel.getName().equals(name))
                            .findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .switchIfEmpty(Mono.empty())
                .delayElement(Duration.ofSeconds(3));
    }

    public Mono<Void> putRedisModel(RedisModel redisModel) {
        return Mono.just(redisModel)
                .filter(r -> StringUtils.isNotEmpty(r.getName()))
                .doOnNext(r -> redisModelList.add(r))
                .then();
    }
}

