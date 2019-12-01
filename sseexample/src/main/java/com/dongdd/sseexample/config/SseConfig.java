package com.dongdd.sseexample.config;

import com.dongdd.sseexample.model.SseModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.FluxSink;

@Configuration
public class SseConfig {

    @Bean
    public EmitterProcessor<SseModel> emitterProcessor() {
        return EmitterProcessor.create(false);
    }

    @Bean
    public DirectProcessor<SseModel> directProcessor() {
        return DirectProcessor.create();
    }

    @Bean
    @Qualifier("eFluxSink")
    public FluxSink<SseModel> emitterProcessorFluxSink(EmitterProcessor<SseModel> emitterProcessor) {
        return emitterProcessor.sink(FluxSink.OverflowStrategy.LATEST);
    }

    @Bean
    @Qualifier("dFluxSink")
    public FluxSink<SseModel> directProcessorFluxSink(DirectProcessor<SseModel> directProcessor) {
        return directProcessor.sink(FluxSink.OverflowStrategy.LATEST);
    }

}
