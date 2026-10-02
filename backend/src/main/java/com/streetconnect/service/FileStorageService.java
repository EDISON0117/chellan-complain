package com.streetconnect.service;

import com.streetconnect.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".webp", ".gif");
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList("image/jpeg", "image/png", "image/webp", "image/gif");

    public FileStorageService(@Value("${app.upload.dir:uploads/}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Failed to upload empty file");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg");

        // Validate extension
        String extension = "";
        int extIndex = originalFilename.lastIndexOf('.');
        if (extIndex > 0) {
            extension = originalFilename.substring(extIndex).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException("Invalid file extension. Allowed: JPG, PNG, WEBP, GIF");
        }

        // Validate content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Invalid file type. Only image files are allowed.");
        }

        // Generate safe unique filename
        String newFilename = UUID.randomUUID() + extension;

        try {
            // Check for path traversal attacks
            if (newFilename.contains("..")) {
                throw new BadRequestException("Filename contains invalid path sequence: " + newFilename);
            }

            Path targetLocation = this.fileStorageLocation.resolve(newFilename);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            return "/uploads/" + newFilename;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + newFilename + ". Please try again!", ex);
        }
    }
}
