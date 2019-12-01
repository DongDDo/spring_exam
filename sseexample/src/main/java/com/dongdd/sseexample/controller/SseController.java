package com.dongdd.sseexample.controller;

import com.dongdd.sseexample.model.SseModel;
import com.dongdd.sseexample.service.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/sse")
public class SseController {

    @Autowired
    private SseService sseService;

    @GetMapping(produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<String> stream(@RequestParam("name") String name, @RequestParam("processor") String processor) {
        return sseService.stream(name, processor);
    }

    @PostMapping
    public Mono<Void> produceData(@RequestBody Mono<SseModel> sseModelMono) {
        return sseModelMono
                .flatMap(sseModel -> sseService.produceData(sseModel));
    }

}
