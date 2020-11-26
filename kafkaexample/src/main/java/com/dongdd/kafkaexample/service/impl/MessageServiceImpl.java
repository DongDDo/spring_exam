package com.dongdd.kafkaexample.service.impl;

import com.dongdd.kafkaexample.model.exception.KafkaException;
import com.dongdd.kafkaexample.service.KafkaService;
import com.dongdd.kafkaexample.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final Sinks.Many<Object> sinksMany;
    private final KafkaService kafkaService;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic}")
    private String topic;

    @Override
    public Mono<String> send(String key, Object value) {
        try {
            return kafkaService.send(topic, key, objectMapper.writeValueAsString(value))
                    .map(b -> {
                        if (b) {
                            return "suceess send message";
                        } else {
                            return "fail send message";
                        }
                    });
        } catch (JsonProcessingException e) {
            return Mono.error(KafkaException.SEND_ERROR);
        }
    }

    @Override
    public Flux<ServerSentEvent<Object>> receive() {
        return sinksMany
                .asFlux()
                .publishOn(Schedulers.parallel())
                .map(message -> ServerSentEvent.builder(message).build())   // Sink로 전송되는 message를 ServerSentEvent로 전송
                .mergeWith(ping())
                .onErrorResume(e -> Flux.empty())
                .doOnCancel(() -> System.out.println("disconnected by client"));    // client 종료 시, ping으로 인지하고 cancel signal을 받음
    }

    private Flux<ServerSentEvent<Object>> ping() {
        return Flux.interval(Duration.ofMillis(500))
                .map(i -> ServerSentEvent.<Object>builder().build());
    }
}



