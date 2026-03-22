package com.instagram_poster.scheduler;

import com.instagram_poster.service.CaptionService;
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
    private final String imageFolder = "//Users/sarthakhejib/Development/test-upload/";


    public PostScheduler(CaptionService captionService, InstagramService instagramService) {
        this.captionService = captionService;
        this.instagramService = instagramService;
    }

    // Runs every Saturday at 7.30 PM
    @Scheduled(cron = "0 30 19 ? * SAT", zone = "Asia/Kolkata")
    public void postImage() {
        File folder = new File(imageFolder);
        File[] files = folder.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));

        if (files == null || files.length == 0) {
            log.info("No images found. Skipping this Saturday...");
            return;
        }

        // Pick only 1 image
        File image = files[0];

        // Generate caption
        log.info("Generating Caption for "+image.getName()+" .");

        String caption = captionService.generateCaption(image);

        // Upload to Instagram
        instagramService.uploadPost(image, caption);
    }
}
