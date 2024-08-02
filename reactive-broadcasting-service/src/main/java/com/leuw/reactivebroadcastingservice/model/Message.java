package com.leuw.reactivebroadcastingservice.model;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface Message {

    @JsonProperty("content")
    String getContent();
}