package com.glimmer.shopping.shoppingmall.util;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileUtil {

    private static final String IMAGE_UPLOAD_PATH = "uploads/images/";
    private static final String[] ALLOWED_IMAGE_EXTENSIONS = {"jpg", "jpeg", "png", "gif"};

    private FileUtil() {
    }

    public static String uploadImage(MultipartFile file, String baseUrl) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("请选择要上传的文件");
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !isValidImageExtension(originalFilename)) {
                throw new IllegalArgumentException("只支持图片文件（jpg/jpeg/png/gif）");
            }

            Path path = Paths.get(IMAGE_UPLOAD_PATH);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String fileName = System.currentTimeMillis() + "_" + originalFilename;
            Path filePath = path.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            return baseUrl + fileName;
        } catch (IOException e) {
            throw new RuntimeException("图片上传失败", e);
        }
    }

    public static ResponseEntity<byte[]> getImage(String fileName) {
        try {
            Path filePath = Paths.get(IMAGE_UPLOAD_PATH + fileName);
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private static boolean isValidImageExtension(String filename) {
        String lowerFilename = filename.toLowerCase();
        for (String extension : ALLOWED_IMAGE_EXTENSIONS) {
            if (lowerFilename.endsWith("." + extension)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidExcelFile(MultipartFile file) {
        if (file.isEmpty()) {
            return false;
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }

        String lowerFilename = originalFilename.toLowerCase();
        return lowerFilename.endsWith(".xlsx") || lowerFilename.endsWith(".xls");
    }
}