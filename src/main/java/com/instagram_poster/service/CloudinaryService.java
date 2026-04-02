package com.instagram_poster.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import com.instagram_poster.dto.ImageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class CloudinaryService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);

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
                            "max_results", 50
                    )
            );

            List<Map> resources = (List<Map>) response.get("resources");

            log.info("Resources: {}", resources);

            if (resources == null || resources.isEmpty()) {
                throw new RuntimeException("No images found in Cloudinary");
            }

            // Filter only kimau_upload folder
            List<Map> filtered = resources.stream()
                    .filter(img -> "kimau_upload".equals(img.get("asset_folder")))
                    .toList();

            if (filtered.isEmpty()) {
                throw new RuntimeException("No images found in kimau_upload folder");
            }

            // Pick random image
            int index = new Random().nextInt(filtered.size());
            Map image = filtered.get(index);

            String imageUrl = image.get("secure_url").toString();
            String publicId = image.get("public_id").toString();
            String assetFolder = image.get("asset_folder").toString();

            // IMPORTANT FIX → build full public_id with folder
            String fullPublicId = assetFolder + "/" + publicId;

            // Extract product name
            String productName = publicId
                    .substring(publicId.lastIndexOf("/") + 1)
                    .replace("-", " ");

            return new ImageData(imageUrl, productName, fullPublicId);

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch images from Cloudinary", e);
        }
    }

    /**
     * Move image to 'posted' folder after successful post
     */
    public void moveToPosted(String publicId) {
        try {
            log.info("Input publicId: {}", publicId);

            // Step 1: Extract filename (safe fallback)
            String fileName = publicId.contains("/")
                    ? publicId.substring(publicId.lastIndexOf("/") + 1)
                    : publicId;

            log.info("Extracted fileName: {}", fileName);

            // Step 2: Try to fetch resource from BOTH possibilities
            Map resource = null;

            try {
                // Try with folder
                resource = cloudinary.api().resource(
                        "kimau_upload/" + fileName,
                        ObjectUtils.emptyMap()
                );
                log.info("Found with folder: kimau_upload/{}", fileName);

            } catch (Exception e1) {

                try {
                    // Try without folder
                    resource = cloudinary.api().resource(
                            fileName,
                            ObjectUtils.emptyMap()
                    );
                    log.info("Found without folder: {}", fileName);

                } catch (Exception e2) {
                    log.error("Resource not found in Cloudinary: {}", fileName);
                    return;
                }
            }

            // Step 3: Get ACTUAL public_id
            String actualPublicId = resource.get("public_id").toString();

            log.info("Actual publicId from Cloudinary: {}", actualPublicId);

            // Step 4: Skip if already moved
            if (actualPublicId.startsWith("kimau_posted/")) {
                log.warn("Already moved: {}", actualPublicId);
                return;
            }

            // Step 5: Rename
            String newPublicId = "kimau_posted/" +
                    actualPublicId.substring(actualPublicId.lastIndexOf("/") + 1);

            log.info("Renaming from {} → {}", actualPublicId, newPublicId);

            cloudinary.uploader().rename(
                    actualPublicId,
                    newPublicId,
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "overwrite", true
                    )
            );

            log.info("SUCCESS: Moved to posted");

        } catch (Exception e) {
            log.error("FINAL ERROR while moving image: {}", publicId, e);
        }
    }
}