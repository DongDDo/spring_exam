package com.dongdd.sseexample.service;

import com.dongdd.sseexample.model.SseModel;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SseService {
    Flux<ServerSentEvent<String>> stream(String name, String processor);

    Mono<Void> produceData(SseModel sseModel);

    Flux<ServerSentEvent<String>> intervalStream();
}
