package com.instagram_poster.scheduler;

import com.instagram_poster.dto.ImageData;
import com.instagram_poster.service.CaptionService;
import com.instagram_poster.service.CloudinaryService;
import com.instagram_poster.service.InstagramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class PostScheduler {

    private static final Logger log = LoggerFactory.getLogger(PostScheduler.class);
    private final CaptionService captionService;
    private final InstagramService instagramService;
    private final CloudinaryService cloudinaryService;

    public PostScheduler(CaptionService captionService, InstagramService instagramService, CloudinaryService cloudinaryService) {
        this.captionService = captionService;
        this.instagramService = instagramService;
        this.cloudinaryService = cloudinaryService;
    }

    @Scheduled(cron = "0 10 1 * * ?", zone = "Asia/Kolkata")
    public void postImage() {
        ImageData image = cloudinaryService.getRandomImage();

        // Generate caption
        log.info("Generating Caption for "+image.getName()+" .");

        String caption = captionService.generateCaption(image.getName());

        // Upload to Instagram
        instagramService.uploadPost(image, caption);
    }
}
