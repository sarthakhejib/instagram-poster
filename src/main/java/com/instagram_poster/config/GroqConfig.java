package com.instagram_poster.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GroqConfig {

    @Value("${groq.api.base-url}")
    private String baseUrl;

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.model}")
    private String model;

    public String getChatCompletionUrl() {
        return baseUrl + "/chat/completions";
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getModel() {
        return model;
    }
}