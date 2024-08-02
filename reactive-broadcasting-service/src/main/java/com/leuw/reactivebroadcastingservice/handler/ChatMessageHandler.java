package com.leuw.reactivebroadcastingservice.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leuw.reactivebroadcastingservice.config.SinkManager;
import com.leuw.reactivebroadcastingservice.model.ChatMessage;
import com.leuw.reactivebroadcastingservice.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatMessageHandler {

    private final SinkManager sinkManager;

    @CrossOrigin
    public Mono<ServerResponse> broadcast(ServerRequest request){
        log.info(request.toString());
        return request.bodyToMono(ChatMessage.class)
                .doOnNext(r -> log.info(r.toString()))
                .flatMap(chatMessage -> sinkManager
                        .getSink(chatMessage.getGroup(), Sinks.many().multicast().onBackpressureBuffer())
                        .flatMap(sink -> Mono.fromRunnable(() -> sink.tryEmitNext(chatMessage))
                                .thenReturn(chatMessage)))
                .flatMap(chatMessage -> ServerResponse.accepted().build())
                .onErrorResume(e -> {
                    log.info(e.getMessage());
                    return ServerResponse.badRequest().build();
                });
    }

}
