package com.dongdd.redis_example.controller;

import com.dongdd.redis_example.model.RedisModel;
import com.dongdd.redis_example.service.LongtimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private LongtimeService longtimeService;

    @GetMapping
    public Mono<String> getRedisModel(@Param("name") String name) {
        return longtimeService.getRedisModel(name)
                .map(redisModel -> redisModel.getName() + " : " + redisModel.getMessage());
    }

    @PostMapping
    public Mono<Void> putRedisModel(@RequestBody RedisModel redisModel) {
        return longtimeService.putRedisModel(redisModel);
    }

}
