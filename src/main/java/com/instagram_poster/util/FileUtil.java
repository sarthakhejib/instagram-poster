package com.instagram_poster.util;

import com.instagram_poster.scheduler.PostScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

public class FileUtil {
    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    public static void moveToPostedFolder(File image, String postedFolderPath) {
        try {
            log.info("Moving to Posted folder");
            // Ensure posted folder exists
            File postedDir = new File(postedFolderPath);
            if (!postedDir.exists()) {
                postedDir.mkdirs();
            }

            // New file name with today's date suffix
            String fileName = image.getName();
            String baseName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
            String extension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : "";
            String newFileName = baseName + "_" + LocalDate.now() + extension;

            Path targetPath = new File(postedDir, newFileName).toPath();

            // Move file
            Files.move(image.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            System.out.println(" Moved " + image.getName() + " → " + targetPath);

        } catch (IOException e) {
           log.error(" Failed to move " + image.getName() + ": " + e.getMessage());
        }
    }
}