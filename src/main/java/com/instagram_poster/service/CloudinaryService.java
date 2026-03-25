package com.instagram_poster.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import com.instagram_poster.dto.ImageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    /**
     * Fetch random image from Cloudinary folder and return URL + name
     */
    public ImageData getRandomImage() {
        try {
            ApiResponse response = cloudinary.api().resources(
                    ObjectUtils.asMap(
                            "type", "upload",
                            "prefix", "kimau_upload",   // your folder
                            "max_results", 50
                    )
            );

            List<Map> resources = (List<Map>) response.get("resources");

            if (resources == null || resources.isEmpty()) {
                throw new RuntimeException("No images found in Cloudinary");
            }

            // Pick random image
            int index = new Random().nextInt(resources.size());
            Map image = resources.get(index);

            String imageUrl = image.get("secure_url").toString();
            String publicId = image.get("public_id").toString();

            // Extract product name
            String productName = publicId
                    .substring(publicId.lastIndexOf("/") + 1)
                    .replace("-", " ");

            return new ImageData(imageUrl, productName, publicId);

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch images from Cloudinary", e);
        }
    }

    /**
     * Move image to 'posted' folder after successful post
     */
    public void moveToPosted(String publicId) {
        try {
            String newPublicId = publicId.replace("kimau_upload/", "kimau_posted/");

            cloudinary.uploader().rename(
                    publicId,
                    newPublicId,
                    ObjectUtils.emptyMap()
            );

            System.out.println("Moved to posted: " + newPublicId);

        } catch (Exception e) {
            System.err.println("Failed to move image: " + publicId);
            e.printStackTrace();
        }
    }
}