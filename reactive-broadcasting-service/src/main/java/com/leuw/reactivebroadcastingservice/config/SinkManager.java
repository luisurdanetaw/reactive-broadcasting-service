package com.leuw.reactivebroadcastingservice.config;

import com.leuw.reactivebroadcastingservice.model.ChatMessage;
import com.leuw.reactivebroadcastingservice.model.Message;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SinkManager {

    private final Map<String, Sinks.Many<ChatMessage>> sinks;

    public SinkManager() {
        this.sinks = new ConcurrentHashMap<>();;
    }

    public Mono<Sinks.Many<ChatMessage>> getSink(String group, Sinks.Many<ChatMessage> newSink) {
        return Mono.fromCallable(() -> sinks.computeIfAbsent(group, key -> newSink));
    }





}