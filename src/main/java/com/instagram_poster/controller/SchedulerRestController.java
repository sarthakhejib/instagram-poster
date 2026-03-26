package com.instagram_poster.controller;

import com.instagram_poster.dto.ImageData;
import com.instagram_poster.service.CaptionService;
import com.instagram_poster.service.CloudinaryService;
import com.instagram_poster.service.InstagramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scheduler")
public class SchedulerRestController {

    private static final Logger log = LoggerFactory.getLogger(SchedulerRestController.class);

    private final CaptionService captionService;
    private final InstagramService instagramService;
    private final CloudinaryService cloudinaryService;

    public SchedulerRestController(CaptionService captionService,
                                   InstagramService instagramService,
                                   CloudinaryService cloudinaryService) {
        this.captionService = captionService;
        this.instagramService = instagramService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping("/post")
    public ResponseEntity<String> postImage() {

        log.info("External scheduler triggered post job");

        ImageData image = cloudinaryService.getRandomImage();

        log.info("Generating Caption for {}", image.getName());

        String caption = captionService.generateCaption(image.getName());

        instagramService.uploadPost(image, caption);

        return ResponseEntity.ok("Post created successfully");
    }
}