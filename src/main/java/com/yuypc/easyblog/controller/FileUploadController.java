package com.yuypc.easyblog.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.yuypc.easyblog.service.UserService;
import com.yuypc.easyblog.utils.security.JwtTokenProvider;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            // Save the file temporarily
            File tempFile = File.createTempFile("avatar", file.getOriginalFilename());
            file.transferTo(tempFile);


            // Upload to OSS
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            String fileName = "avatars/" + tempFile.getName();
            ossClient.putObject(bucketName, fileName, tempFile);
            Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 30);
            String avatarUrl = ossClient.generatePresignedUrl(bucketName, fileName, expiration).toString(); // Generate URL valid for 1 month

            // Clean up local file
            tempFile.delete();
            ossClient.shutdown();

            String currentUsername = getCurrentUsername();

            userService.updateAvatar(currentUsername, avatarUrl);

            String newJwt = jwtTokenProvider.createToken(currentUsername, avatarUrl);
            return ResponseEntity.ok(new AvatarResponse(avatarUrl, newJwt));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to upload file");
        }
    }

    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadPic(MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            // Save the file temporarily
            File tempFile = File.createTempFile("pic", file.getOriginalFilename());
            file.transferTo(tempFile);

            String currentUsername = getCurrentUsername();

            // Upload to OSS
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            String fileName = currentUsername+"/article/pic/" + tempFile.getName();
            ossClient.putObject(bucketName, fileName, tempFile);
            Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 30);
            String picUrl = ossClient.generatePresignedUrl(bucketName, fileName, expiration).toString(); // Generate URL valid for 1 month

            // Clean up local file
            tempFile.delete();
            ossClient.shutdown();


            return ResponseEntity.ok(new UploadPicResponse(picUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to upload file");
        }
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
    private static class UploadPicResponse{
        private String picUrl;

        public UploadPicResponse(String url) {
            this.picUrl = url;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

    }

    private static class AvatarResponse {
        private String avatarUrl;

        private String token;

        public AvatarResponse(String avatarUrl, String token) {
            this.avatarUrl = avatarUrl;
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }
    }
}
