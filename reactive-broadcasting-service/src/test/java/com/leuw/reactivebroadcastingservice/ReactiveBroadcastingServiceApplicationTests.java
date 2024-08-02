package com.leuw.reactivebroadcastingservice;

import com.leuw.reactivebroadcastingservice.model.ChatMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SpringBootTest
@AutoConfigureWebTestClient
class ReactiveBroadcastingServiceApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    private static ReactorNettyWebSocketClient wsClient;
    private static URI wsUri;

    private static Flux<ChatMessage> payloads;

    @BeforeAll
    static void setup() throws Exception {
        wsUri = new URI("ws://localhost:8081/ws/chat");
        wsClient = new ReactorNettyWebSocketClient();
        payloads = Flux.range(0,10)
                .map(i -> ChatMessage.builder()
                        .id(UUID.randomUUID())
                        .username("User" + i)
                        .content("Test Message" + i)
                        .group("Group" + i)
                        .date(LocalDateTime.now())
                        .build()
                );
    }

    @Test
    void contextLoads() {}

    @Test
    void testMessageBroadcasting(){
        Mono<Void> sessionMono = wsClient.execute(wsUri,
                session -> session.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .doOnNext(message -> Assertions.assertEquals("e", message))
                        .then()
        );

        sessionMono.subscribe();
/*
        payloads.flatMap(p ->
            webTestClient.post()
                    .uri("/chat/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(p)
                    .exchange()
                    .expectStatus().isAccepted()
        ).blockLast();*/





    }

}
