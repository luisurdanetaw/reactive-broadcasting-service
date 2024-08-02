package com.leuw.reactivebroadcastingservice.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leuw.reactivebroadcastingservice.handler.ChatMessageHandler;
import com.leuw.reactivebroadcastingservice.handler.ChatMessageWebSocketHandler;
import com.leuw.reactivebroadcastingservice.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Slf4j
@Configuration
public class ChatMessageRouter {

    @Bean
    public RouterFunction<ServerResponse> broadcastRoute(ChatMessageHandler handler) {
        return RouterFunctions.route(POST("/chat/send"), handler::broadcast);
    }

}
