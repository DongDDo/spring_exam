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

    // autocanel 설정: true - subscribe가 하나도 없으면 processor 종료, false - subscribe가 없더라 processor 유지
    @Bean
    public EmitterProcessor<SseModel> emitterProcessor() {
        return EmitterProcessor.create(false);
    }

    @Bean
    public DirectProcessor<SseModel> directProcessor() {
        return DirectProcessor.create();
    }

    // OverFlow가 생길 경우, 과거의 데이터를 버리고 최신 데이터만 유지
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
