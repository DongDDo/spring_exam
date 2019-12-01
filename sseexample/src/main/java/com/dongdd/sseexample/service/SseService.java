package com.dongdd.sseexample.service;

import com.dongdd.sseexample.model.SseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SseService {
    Flux<String> stream(String name, String processor);

    Mono<Void> produceData(SseModel sseModel);
}
