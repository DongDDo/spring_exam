package com.dongdd.kafkaexample.service;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessageService {

    Mono<String> send(String key, Object value);

    Flux<ServerSentEvent<Object>> receive();
}
