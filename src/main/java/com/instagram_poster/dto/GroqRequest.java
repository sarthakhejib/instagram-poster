package com.instagram_poster.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class GroqRequest {

    private String model;

    private List<Message> messages;

    @JsonProperty("max_tokens")
    private int maxTokens;

    private double temperature;

    public GroqRequest(String model,
                       List<Message> messages,
                       int maxTokens,
                       double temperature) {
        this.model = model;
        this.messages = messages;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
    }

    public String getModel() {
        return model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public double getTemperature() {
        return temperature;
    }

    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }
}