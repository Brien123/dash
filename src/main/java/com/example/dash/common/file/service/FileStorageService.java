package com.example.dash.common.file.service;

import jakarta.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${file.upload.directory:${user.home}/.dash/uploads}")
    private String uploadDirectory;

    private Path uploadPath;

    @PostConstruct
    public void init() {
        uploadPath = Paths.get(uploadDirectory).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
            log.info("File upload directory initialized at: {}", uploadPath);
        } catch (IOException e) {
            throw new RuntimeException(
                "Could not create upload directory: " + uploadPath,
                e
            );
        }
    }

    public String store(MultipartFile file) {
        return store(file, null);
    }

    public String store(MultipartFile file, String subdirectory) {
        String originalFilename = StringUtils.cleanPath(
            Objects.requireNonNull(file.getOriginalFilename())
        );
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }
        String storedFilename = UUID.randomUUID() + extension;

        Path targetPath = uploadPath;
        if (subdirectory != null && !subdirectory.isBlank()) {
            targetPath = uploadPath.resolve(subdirectory);
            try {
                Files.createDirectories(targetPath);
            } catch (IOException e) {
                throw new RuntimeException(
                    "Could not create subdirectory: " + targetPath,
                    e
                );
            }
        }

        Path destination = targetPath.resolve(storedFilename);
        try {
            Files.copy(
                file.getInputStream(),
                destination,
                StandardCopyOption.REPLACE_EXISTING
            );
            log.info("File stored at: {}", destination);
        } catch (IOException e) {
            throw new RuntimeException(
                "Could not store file " + originalFilename,
                e
            );
        }

        return subdirectory != null
            ? subdirectory + "/" + storedFilename
            : storedFilename;
    }

    public Resource load(String filename) {
        return load(filename, null);
    }

    public Resource load(String filename, String subdirectory) {
        Path filePath = uploadPath;
        if (subdirectory != null && !subdirectory.isBlank()) {
            filePath = filePath.resolve(subdirectory);
        }
        filePath = filePath.resolve(filename).normalize();

        if (!filePath.startsWith(uploadPath)) {
            throw new SecurityException(
                "Cannot access file outside upload directory"
            );
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException(
                    "File not found or not readable: " + filename
                );
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }

    public void delete(String filename) {
        delete(filename, null);
    }

    public void delete(String filename, String subdirectory) {
        Path filePath = uploadPath;
        if (subdirectory != null && !subdirectory.isBlank()) {
            filePath = filePath.resolve(subdirectory);
        }
        filePath = filePath.resolve(filename).normalize();

        if (!filePath.startsWith(uploadPath)) {
            throw new SecurityException(
                "Cannot delete file outside upload directory"
            );
        }

        try {
            Files.deleteIfExists(filePath);
            log.info("File deleted: {}", filePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file: " + filename, e);
        }
    }

    public Path getUploadPath() {
        return uploadPath;
    }

    public Map<String, String> storeWithCompression(MultipartFile file) {
        return storeWithCompression(file, null);
    }

    public Map<String, String> storeWithCompression(MultipartFile file, String subdirectory) {
        try {
            BufferedImage original = ImageIO.read(file.getInputStream());
            if (original == null) {
                throw new RuntimeException("Could not read image from file: " + file.getOriginalFilename());
            }

            ByteArrayOutputStream thumbOut = new ByteArrayOutputStream();
            Thumbnails.of(original)
                .size(150, 150)
                .outputQuality(0.3)
                .outputFormat("jpg")
                .toOutputStream(thumbOut);

            ByteArrayOutputStream mediumOut = new ByteArrayOutputStream();
            Thumbnails.of(original)
                .scale(1)
                .outputQuality(0.7)
                .outputFormat("jpg")
                .toOutputStream(mediumOut);

            Map<String, String> paths = new LinkedHashMap<>();
            paths.put("thumb", store(new ByteArrayMultipartFile(thumbOut.toByteArray(), "thumb.jpg"), subdirectory));
            paths.put("medium", store(new ByteArrayMultipartFile(mediumOut.toByteArray(), "medium.jpg"), subdirectory));
            return paths;
        } catch (IOException e) {
            throw new RuntimeException("Failed to compress image", e);
        }
    }

    static class ByteArrayMultipartFile implements MultipartFile {
        private final byte[] content;
        private final String filename;

        ByteArrayMultipartFile(byte[] content, String filename) {
            this.content = content;
            this.filename = filename;
        }

        @Override
        public String getName() {
            return filename;
        }

        @Override
        public String getOriginalFilename() {
            return filename;
        }

        @Override
        public String getContentType() {
            return "image/jpeg";
        }

        @Override
        public boolean isEmpty() {
            return content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() {
            return content;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(File dest) throws IOException {
            Files.write(dest.toPath(), content);
        }
    }
}
