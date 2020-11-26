package com.dongdd.kafkaexample.controller;

import com.dongdd.kafkaexample.model.Message;
import com.dongdd.kafkaexample.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/kafka")
@RequiredArgsConstructor
public class KafkaController {

    private final MessageService messageService;

    @PostMapping
    public Mono<String> produceMessage(@RequestBody Mono<Message> message) {
        return message
                .flatMap(msg -> messageService.send(msg.getName(), msg));
    }

    @GetMapping
    public Flux<ServerSentEvent<Object>> consumeMessage() {
        return messageService.receive();
    }
}
