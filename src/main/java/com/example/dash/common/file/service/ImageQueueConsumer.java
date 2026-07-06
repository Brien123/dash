package com.example.dash.common.file.service;

import jakarta.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageQueueConsumer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final FileStorageService fileStorageService;
    private final Map<String, ImageCompressionHandler> compressionHandlers;

    private static final String QUEUE_KEY = "image:queue";

    @PostConstruct
    public void start() {
        Thread consumer = new Thread(this::consumeLoop, "image-consumer");
        consumer.setDaemon(true);
        consumer.start();
    }

    private void consumeLoop() {
        while (true) {
            try {
                ImageTask task = (ImageTask) redisTemplate
                        .opsForList().leftPop(QUEUE_KEY, 5, TimeUnit.SECONDS);
                while (task != null) {
                    processTask(task);
                    task = (ImageTask) redisTemplate
                            .opsForList().leftPop(QUEUE_KEY, 0, TimeUnit.SECONDS);
                }
            } catch (Exception e) {
                log.error("Image consumer error", e);
            }
        }
    }

    private void processTask(ImageTask task) {
        try {
            switch (task.getType()) {
                case "compress" -> processCompression(task);
                default -> log.warn("Unknown image task type: {}", task.getType());
            }
        } catch (Exception e) {
            log.error("Failed to process image task for {}", task.getStoredPath(), e);
        }
    }

    private void processCompression(ImageTask task) throws Exception {
        Resource original = fileStorageService.load(task.getStoredPath());

        BufferedImage image;
        try (InputStream in = original.getInputStream()) {
            image = ImageIO.read(in);
        }
        if (image == null) {
            log.error("Could not read image: {}", task.getStoredPath());
            return;
        }

        String storedPath = task.getStoredPath();
        String subdirectory = null;
        int slashIndex = storedPath.lastIndexOf('/');
        if (slashIndex > 0) {
            subdirectory = storedPath.substring(0, slashIndex);
        }

        ByteArrayOutputStream thumbOut = new ByteArrayOutputStream();
        Thumbnails.of(image)
            .size(150, 150)
            .outputQuality(0.3)
            .outputFormat("jpg")
            .toOutputStream(thumbOut);

        ByteArrayOutputStream mediumOut = new ByteArrayOutputStream();
        Thumbnails.of(image)
            .scale(1)
            .outputQuality(0.5)
            .outputFormat("jpg")
            .toOutputStream(mediumOut);

        String thumbPath = fileStorageService.store(
            new FileStorageService.ByteArrayMultipartFile(thumbOut.toByteArray(), "thumb.jpg"),
            subdirectory + "/thumb"
        );
        String mediumPath = fileStorageService.store(
            new FileStorageService.ByteArrayMultipartFile(mediumOut.toByteArray(), "medium.jpg"),
            subdirectory + "/medium"
        );

        ImageCompressionHandler handler = compressionHandlers.get(task.getEntityType());
        if (handler != null) {
            handler.onCompressionComplete(task.getEntityId(), storedPath, thumbPath, mediumPath);
            log.info("Updated {} {} with thumb and medium URLs", task.getEntityType(), task.getEntityId());
        } else {
            log.warn("No compression handler for entity type: {}", task.getEntityType());
        }

        log.info("Compressed images created for {}", storedPath);
    }
}
