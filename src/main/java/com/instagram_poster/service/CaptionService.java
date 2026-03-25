package com.instagram_poster.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.instagram_poster.config.GroqConfig;
import com.instagram_poster.dto.GroqRequest;
import com.instagram_poster.dto.GroqResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class CaptionService {

    private static final Logger log = LoggerFactory.getLogger(CaptionService.class);

    private final GroqConfig groqConfig;
    private final RestTemplate restTemplate;

    public CaptionService(GroqConfig groqConfig, RestTemplate restTemplate) {
        this.groqConfig = groqConfig;
        this.restTemplate = restTemplate;
    }

    public String generateCaption(String imageName) {
        try {
            String fileName = imageName
                    .replace(".jpg", "")
                    .replace(".png", "");

            String prompt =
                    "Generate a highly engaging Instagram caption for: " + fileName + ". " +
                            "Rules: " +
                            "1. Strong hook in first line. " +
                            "2. Emotional and catchy tone. " +
                            "3. Include a call-to-action to increase comments. " +
                            "4. Add exactly 9 hashtags using this structure: " +
                            "- 3 high-reach hashtags, " +
                            "- 3 medium-reach hashtags, " +
                            "- 3 niche-specific hashtags related to the subject. " +
                            "5. Do not repeat hashtags. " +
                            "6. Keep caption clean and not spammy."+
                            "7. Do not add image file name in the caption."+
                            "8. Do not add numbers in the hashtags"+
                            "9. Stick to the image name";

            // Groq request
            GroqRequest request = new GroqRequest(
                    groqConfig.getModel(),
                    List.of(new GroqRequest.Message("user", prompt)),
                    100,     // max tokens (perfect for captions)
                    0.8     // creativity
            );

            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(groqConfig.getApiKey());

            ObjectMapper mapper = new ObjectMapper();
            log.info("Groq request JSON: {}", mapper.writeValueAsString(request));

            HttpEntity<GroqRequest> entity = new HttpEntity<>(request, headers);

            // API call
            ResponseEntity<GroqResponse> response = restTemplate.exchange(
                    groqConfig.getChatCompletionUrl(),
                    HttpMethod.POST,
                    entity,
                    GroqResponse.class
            );

            // Extract caption
            if (response.getBody() != null
                    && response.getBody().getChoices() != null
                    && !response.getBody().getChoices().isEmpty()) {

                String caption = response.getBody()
                        .getChoices()
                        .get(0)
                        .getMessage()
                        .getContent();

                log.info("Generated caption for {}: {}", fileName, caption);
                return caption;
            }

            throw new RuntimeException("Empty response from Groq API");

        } catch (Exception e) {
            log.error("Error generating caption for image: {}", imageName, e);
            throw new RuntimeException("Failed to generate caption: " + e.getMessage());
        }
    }
}