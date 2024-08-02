package com.leuw.reactivebroadcastingservice.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leuw.reactivebroadcastingservice.config.SinkManager;
import com.leuw.reactivebroadcastingservice.model.ChatMessage;
import com.leuw.reactivebroadcastingservice.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageWebSocketHandler implements WebSocketHandler {

    private final SinkManager sinkManager;

    @CrossOrigin
    @NonNull
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String group = UriComponentsBuilder.fromUri(session.getHandshakeInfo().getUri())
                .build()
                .getQueryParams()
                .getFirst("group");

        log.info("WebSocket connection established for group: {}", group);

        return sinkManager.getSink(group, Sinks.many().multicast().onBackpressureBuffer())
                .flatMap(sink -> session.send(
                        sink.asFlux()
                                .flatMap(message ->
                                        Mono.fromCallable(message::toJson)
                                                .map(session::textMessage)
                                )
                                .doOnNext(msg -> log.info(String.valueOf(msg)))
                                .doOnError(e-> log.info(e.getMessage()))
                ));
    }
}
