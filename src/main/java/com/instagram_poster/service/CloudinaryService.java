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

            // ✅ Filter only kimau_upload folder
            List<Map> filtered = resources.stream()
                    .filter(img -> "kimau_upload".equals(img.get("asset_folder")))
                    .toList();

            if (filtered.isEmpty()) {
                throw new RuntimeException("No images found in kimau_upload folder");
            }

            // ✅ Pick random image
            int index = new Random().nextInt(filtered.size());
            Map image = filtered.get(index);

            String imageUrl = image.get("secure_url").toString();
            String publicId = image.get("public_id").toString();
            String assetFolder = image.get("asset_folder").toString();

            // ✅ IMPORTANT FIX → build full public_id with folder
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
            // Already moved check
            if (publicId.startsWith("kimau_posted/")) {
                log.warn("Already moved: {}", publicId);
                return;
            }

            log.info("Trying to move publicId: {}", publicId);

            // Extract only file name (remove folder path)
            String fileName = publicId.contains("/")
                    ? publicId.substring(publicId.lastIndexOf("/") + 1)
                    : publicId;

            // Build new public ID
            String newPublicId = "kimau_posted/" + fileName;

            log.info("Renaming from {} to {}", publicId, newPublicId);

            // Rename with correct options
            cloudinary.uploader().rename(
                    publicId,              // MUST be exact existing ID
                    newPublicId,
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "overwrite", true
                    )
            );

            log.info("Moved to posted: {} -> {}", publicId, newPublicId);

        } catch (Exception e) {
            log.error("Failed to move image: {}", publicId, e);
        }
    }
}