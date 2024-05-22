package com.chapssal.video;

import com.chapssal.user.User;
import com.chapssal.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Controller
public class VideoController {

    private static final String TEMP_FOLDER = System.getProperty("java.io.tmpdir");

    private final VideoService videoService;
    private final S3Service s3Service;
    private final UserService userService;

    @Autowired
    public VideoController(VideoService videoService, S3Service s3Service, UserService userService) {
        this.videoService = videoService;
        this.s3Service = s3Service;
        this.userService = userService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/video/upload")
    public String videoUpload(@RequestParam("file") MultipartFile file,
                              @RequestParam("title") String title,
                              @RequestParam("topic") Integer topic,
                              @RequestParam("thumbnail") String thumbnailBase64,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        if (file.isEmpty() || thumbnailBase64.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/";
        }

        User user = userService.getUser(principal.getName());

        try {
            // 비디오 파일 임시 저장
            String videoFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path videoTempFilePath = Paths.get(TEMP_FOLDER, videoFileName);
            Files.write(videoTempFilePath, file.getBytes());

            // 썸네일 파일 임시 저장
            byte[] thumbnailBytes = Base64.getDecoder().decode(thumbnailBase64.split(",")[1]);
            String thumbnailFileName = UUID.randomUUID().toString() + ".jpg";
            Path thumbnailTempFilePath = Paths.get(TEMP_FOLDER, thumbnailFileName);
            Files.write(thumbnailTempFilePath, thumbnailBytes);

            // S3에 비디오 파일 업로드
            File videoTempFile = videoTempFilePath.toFile();
            String videoS3Key = "videos/" + videoFileName;
            s3Service.uploadFile(videoS3Key, videoTempFile);

            // S3에 썸네일 파일 업로드
            File thumbnailTempFile = thumbnailTempFilePath.toFile();
            String thumbnailS3Key = "thumbnails/" + thumbnailFileName;
            s3Service.uploadFile(thumbnailS3Key, thumbnailTempFile);

            // 업로드된 파일 URL 가져오기
            String videoFileUrl = s3Service.getFileUrl(videoS3Key);
            String thumbnailFileUrl = s3Service.getFileUrl(thumbnailS3Key);

            // Video 객체 생성 및 저장
            Video video = new Video();
            video.setUser(user.getUserNum());
            video.setTitle(title);
            video.setTopic(topic);
            video.setUploadDate(LocalDateTime.now());
            video.setVideoUrl(videoFileUrl);
            video.setThumbnailUrl(thumbnailFileUrl);
            video.setViewCount(0); // 초기 조회수 설정

            videoService.create(video);

            // 임시 파일 삭제
            Files.delete(videoTempFilePath);
            Files.delete(thumbnailTempFilePath);

            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "' with thumbnail");

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "File upload failed: " + e.getMessage());
        }

        return "redirect:/";
    }
}
