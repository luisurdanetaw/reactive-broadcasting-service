package com.leuw.reactivebroadcastingservice.config;

import com.leuw.reactivebroadcastingservice.model.ChatMessage;
import com.leuw.reactivebroadcastingservice.model.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Sinks;

import java.util.Map;

@Configuration
public class WebSocketConfig{

    @Bean
    public SimpleUrlHandlerMapping handlerMapping(WebSocketHandler wsHandler){
        return new SimpleUrlHandlerMapping(Map.of("/ws/chat", wsHandler), 1);
    }

    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter(){
        return new WebSocketHandlerAdapter();
    }
}
