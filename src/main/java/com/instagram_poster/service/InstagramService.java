package com.instagram_poster.service;

import com.instagram_poster.dto.ImageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class InstagramService {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Value("${instagram.access-token}")
    private String accessToken;

    @Value("${instagram.business-id}")
    private String instagramBusinessId;

    private static final Logger log = LoggerFactory.getLogger(InstagramService.class);

    private final RestTemplate restTemplate;

    public InstagramService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetch image from Cloudinary → Post to Instagram → Move to 'posted'
     */
    public void uploadPost(ImageData image,String caption) {
        try {
            String publicId = image.getPublicId();

            String watermarkedUrl = cloudinaryService.getWatermarkedImageUrl(publicId);

            // ✅ 2. Transform for Instagram ratio
            String imageUrl = transformToInstagramRatio(watermarkedUrl);

            log.info("Image URL: {}", imageUrl);

            // ✅ 3. Create media object
            String uploadUrl = "https://graph.facebook.com/v19.0/" + instagramBusinessId + "/media";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("image_url", imageUrl);
            body.add("caption", caption);
            body.add("access_token", accessToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> uploadResponse = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            log.info("Upload response: {}", uploadResponse.getBody());

            Map<String, Object> uploadBody = uploadResponse.getBody();

            if (uploadBody == null || !uploadBody.containsKey("id")) {
                throw new RuntimeException("Failed to create media object");
            }

            String creationId = uploadBody.get("id").toString();
            log.info("Creation ID: {}", creationId);

            // ⏳ small delay
            Thread.sleep(3000);

            // ✅ 4. Publish
            String publishUrl = "https://graph.facebook.com/v19.0/"
                    + instagramBusinessId
                    + "/media_publish"
                    + "?creation_id=" + creationId
                    + "&access_token=" + accessToken;

            ResponseEntity<Map> publishResponse = restTemplate.exchange(
                    publishUrl,
                    HttpMethod.POST,
                    null,
                    Map.class
            );

            log.info("Publish response: {}", publishResponse.getBody());

            if (publishResponse.getStatusCode().is2xxSuccessful()) {
                log.info("Posted successfully to kimau_creations!");

                // Move only after success
                cloudinaryService.moveToPosted(publicId);
            } else {
                log.error("Post failed, not moving image");
            }

        } catch (Exception e) {
            log.error("Error while posting to Instagram", e);
        }
    }

    /**
     * Maintain Instagram aspect ratio (no cropping loss)
     */
    private String transformToInstagramRatio(String url) {
        if (url == null || !url.contains("/upload/")) {
            return url;
        }

        return url.replace(
                "/upload/",
                "/upload/w_1080,h_1350,c_fill,g_auto:subject/"
        );
    }
}