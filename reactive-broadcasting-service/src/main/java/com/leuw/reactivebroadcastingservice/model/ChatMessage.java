package com.leuw.reactivebroadcastingservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Value
@ToString
public class ChatMessage implements Message {


    @Id
    @JsonProperty("id")
    UUID id;

    @JsonProperty("date")
    LocalDateTime date;

    @JsonProperty("username")
    String username;

    @JsonProperty("content")
    String content;

    @JsonProperty("group")
    String group;

    public ChatMessage(String username, String content, String group) {
        this.id = UUID.randomUUID();
        this.date = LocalDateTime.now();
        this.username = username;
        this.content = content;
        this.group = group;
    }

    public String toJson() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"id\":\"").append(id.toString()).append("\",")
                .append("\"date\":\"").append(date.format(formatter)).append("\",")
                .append("\"username\":\"").append(username).append("\",")
                .append("\"content\":\"").append(content).append("\",")
                .append("\"group\":\"").append(group).append("\"")
                .append("}");
        return json.toString();
    }
}
