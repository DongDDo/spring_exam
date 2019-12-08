package com.dongdd.sseexample.service.impl;

import com.dongdd.sseexample.model.SseModel;
import com.dongdd.sseexample.service.SseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.*;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Service
public class SseServiceImpl implements SseService {

    @Autowired
    private EmitterProcessor<SseModel> emitterProcessor;

    @Autowired
    private DirectProcessor<SseModel> directProcessor;

    @Autowired
    @Qualifier("dFluxSink")
    private FluxSink<SseModel> directProcessorFluxSink;

    @Autowired
    @Qualifier("eFluxSink")
    private FluxSink<SseModel> emitterProcessorFluxSink;

    private static final String EMITTER_PROCESSOR = "emitter";
    private static final String DIRECT_PROCESSOR = "direct";

    @Override
    public Flux<ServerSentEvent<String>> stream(String name, String processor) {
        if (StringUtils.isEmpty(name)) {
            return Flux.error(new IllegalStateException("invalid name"));
        }
        if (EMITTER_PROCESSOR.equals(processor)) {
            return emitterProcessor
                    .publishOn(Schedulers.elastic())
                    .filter(sseModel -> name.equals(sseModel.getName()))
                    .map(sseModel -> ServerSentEvent.builder(sseModel.getMessage()).build())
                    .mergeWith(ping())
                    .doOnCancel(() -> System.out.println("Client is disconnected"));
        } else if (DIRECT_PROCESSOR.equals(processor)) {
            return directProcessor
                    .publishOn(Schedulers.elastic())
                    .filter(sseModel -> name.equals(sseModel.getName()))
                    .map(sseModel -> ServerSentEvent.builder(sseModel.getMessage()).build())
                    .mergeWith(ping())
                    .doOnCancel(() -> System.out.println("Client is disconnected"));
        }

        return Flux.error(new IllegalStateException("invalid processor"));
    }

    @Override
    public Mono<Void> produceData(SseModel sseModel) {
        directProcessorFluxSink.next(sseModel);
        emitterProcessorFluxSink.next(sseModel);
        return Mono.empty();
    }

    @Override
    public Flux<ServerSentEvent<String>> intervalStream() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(i -> ServerSentEvent.builder("data " + i).build());
    }

    private  Flux<ServerSentEvent<String>> ping() {
        return Flux.interval(Duration.ofMillis(500))
                .map(i -> ServerSentEvent.<String>builder().build());
    }
}
