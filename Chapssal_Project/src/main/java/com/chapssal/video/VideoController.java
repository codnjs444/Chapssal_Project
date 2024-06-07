package com.chapssal.video;

import com.chapssal.comment.Comment;
import com.chapssal.comment.CommentLikeService;
import com.chapssal.comment.CommentService;
import com.chapssal.comment.RCommentService;
import com.chapssal.follow.FollowService;
import com.chapssal.hashtag.HashtagService;
import com.chapssal.topic.SelectedTopicService;
import com.chapssal.user.User;
import com.chapssal.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
public class VideoController {

    private static final String TEMP_FOLDER = System.getProperty("java.io.tmpdir");
    private final VideoService videoService;
    private final S3Service s3Service;
    private final UserService userService;
    private final SelectedTopicService selectedTopicService;

    private final CommentLikeService commentLikeService;
    

    private final HashtagService hashtagService;
    
    @Autowired
    private FollowService followService;
    
    @Autowired
    private VideoLikeService videoLikeService;
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private RCommentService rCommentService;
    
    public VideoController(VideoService videoService, S3Service s3Service, UserService userService, SelectedTopicService selectedTopicService, HashtagService hashtagService, CommentLikeService commentLikeService) {
        this.videoService = videoService;
        this.s3Service = s3Service;
        this.userService = userService;
        this.selectedTopicService = selectedTopicService;
        this.hashtagService = hashtagService;
        this.commentLikeService = commentLikeService;
    }
    
//    @ModelAttribute("topicsByVoteCount")
//    public List<Object[]> topicsByVoteCount() {
//        return selectedTopicService.findTopicsByVoteCount();
//    	return selectedTopicService.getTopicsByVoteCountForLastWeek();
//    }

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
            video.setUser(user);
            video.setTitle(title);
            video.setTopic(topic);
            video.setUploadDate(LocalDateTime.now());
            video.setVideoUrl(videoFileUrl);
            video.setThumbnailUrl(thumbnailFileUrl);
            video.setViewCount(0); // 초기 조회수 설정

            videoService.create(video);

            // 해시태그 처리
            hashtagService.extractAndSaveHashtags(title, video);
            
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

    @GetMapping("/explore/{videoNum}")
    public String viewVideo(@PathVariable("videoNum") int videoNum, Model model) {
        Video video = videoService.findById(videoNum).orElseThrow(() -> new RuntimeException("Video not found"));
        User user = video.getUser();

        model.addAttribute("video", video);    
        model.addAttribute("uploader", user);
        return "explore"; // 여기서 home.html을 explore.html로 변경했습니다.
    }
    
    @GetMapping("/explore")
    public String viewExplorePage(Model model) {
        List<Object[]> topicsByVoteCount = selectedTopicService.getTopicsByVoteCountForLastWeek();
        model.addAttribute("topicsByVoteCount", topicsByVoteCount);

        List<VideoService.VideoWithLikesAndComments> videosWithLikesAndComments = videoService.getAllVideosOrderedByLikes();
        model.addAttribute("videos", videosWithLikesAndComments); // 섞인 비디오를 모델에 추가
        return "explore"; // explore.html 템플릿을 렌더링
    }
    

    @DeleteMapping("/video/delete/{videoNum}")
    @ResponseBody
    public Map<String, Object> deleteVideo(@PathVariable("videoNum") int videoNum) {
        Map<String, Object> response = new HashMap<>();
        try {
            videoService.delete(videoNum);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/following")
    public String viewFollowPage(Model model, Principal principal) {
        User currentUser = userService.getUser(principal.getName());
        List<User> followingUsers = followService.getFollowingUsers(currentUser.getUserNum());
        List<VideoService.VideoWithLikesAndComments> videosWithLikesAndComments = videoService.getVideosWithLikeAndCommentCounts(followingUsers);
        model.addAttribute("videos", videosWithLikesAndComments);
        return "following"; // following.html 템플릿을 렌더링
    }

    @GetMapping("/bestvideos")
    public String viewBestVideosPage(@RequestParam(value = "weekOffset", defaultValue = "0") int weekOffset, Model model) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate startOfWeek = monday.minusWeeks(weekOffset + 1); // 저번 주 월요일
        LocalDate endOfWeek = startOfWeek.plusDays(6); // 저번 주 일요일

        LocalDateTime startDateTime = startOfWeek.atStartOfDay();
        LocalDateTime endDateTime = endOfWeek.atTime(LocalTime.MAX);

        List<Object[]> topVideos = videoService.findTopVideosForWeek(startDateTime, endDateTime);
        List<Object[]> topicsByVoteCount = selectedTopicService.getTopicsByVoteCountForWeek(startDateTime, endDateTime);

        int weekOfMonth = (startOfWeek.getDayOfMonth() - 1) / 7 + 1;
        String currentWeek = String.format("%d년 %02d월 %d주차",
                startOfWeek.getYear(),
                startOfWeek.getMonthValue(),
                weekOfMonth);

        model.addAttribute("topVideos", topVideos);
        model.addAttribute("topicsByVoteCount", topicsByVoteCount);
        model.addAttribute("weekOffset", weekOffset);
        model.addAttribute("currentWeek", currentWeek);

        return "bestvideos";
    }
    
    @GetMapping("/video/{videoNum}")
    public String getVideoPage(@PathVariable("videoNum") int videoNum, @RequestParam("userNum") int userNum, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";  // 로그인 페이지로 리다이렉트
        }
        String currentUsername = authentication.getName();  // 로그인한 사용자의 이름 가져오기
        Optional<User> currentUserOptional = userService.findByUserId2(currentUsername);
        Optional<Video> videoOptional = videoService.findById(videoNum);
        
        if (!currentUserOptional.isPresent()) {
            return "redirect:/"; // 사용자가 없으면 홈으로 리다이렉트
        }
        if (!videoOptional.isPresent()) {
            return "error/404"; // 비디오가 없을 경우 404 페이지로 이동
        }
        
        User currentUser = currentUserOptional.get();
        Integer currentUserNum = currentUser.getUserNum(); // 현재 로그인한 사용자의 userNum
        Video video = videoOptional.get();

        // 프로필 페이지의 사용자 정보를 가져옴
        User user = userService.findByUserNum(userNum);
        if (user == null) {
            return "redirect:/"; // 사용자를 찾을 수 없으면 홈으로 리다이렉트
        }
        
        String userName = user.getUserName();
        String schoolName = user.getSchool().getSchoolName();
        String profilePictureUrl = user.getProfilePictureUrl(); // 상대방의 프로필 사진 URL을 가져옴
        
        model.addAttribute("userName", userName);
        model.addAttribute("schoolName", schoolName);
        model.addAttribute("profilePictureUrl", profilePictureUrl); // 프로필 사진 URL 추가
        model.addAttribute("userNum", userNum); // 조회된 사용자의 userNum
        model.addAttribute("videoUrl", video.getVideoUrl());
        model.addAttribute("videoTitle", video.getTitle());
        model.addAttribute("videoUser", video.getUser());
        
        // 팔로우 상태 추가
        boolean isFollowing = followService.isFollowing(currentUserNum, userNum);
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("currentUserNum", currentUserNum); // 현재 로그인한 사용자의 userNum 추가

        List<User> followingUsers = followService.getFollowingUsers(userNum);
        List<User> followerUsers = followService.getFollowerUsers(userNum);

        model.addAttribute("followingUsers", followingUsers);
        model.addAttribute("followerUsers", followerUsers);

        // 좋아요 상태 확인
        boolean isLiked = videoLikeService.isLikedByUser(videoNum, currentUserNum);
        model.addAttribute("isLiked", isLiked);

        // 좋아요 수 카운트
        int likeCount = videoLikeService.countLikesByVideoId(videoNum);
        model.addAttribute("likeCount", likeCount);
        
        // 댓글 목록을 모델에 추가
        List<Comment> comments = commentService.findByVideoNum(videoNum);
        for (Comment comment : comments) {
            boolean isCommentLiked = commentLikeService.isCommentLikedByUser(comment.getCommentNum(), currentUserNum);
            comment.setLiked(isCommentLiked);

            // 댓글에 답글이 있는지 확인하여 모델에 추가
            boolean hasReplies = commentService.hasReplies(comment.getCommentNum());
            comment.setHasReplies(hasReplies);
        }
        commentService.setLikeCountsForComments(comments);
        model.addAttribute("comments", comments);
        
        // 댓글 수 추가
        int commentCount = commentService.countCommentsByVideoNum(videoNum);
        model.addAttribute("commentCount", commentCount);
        
        // 이전 및 다음 비디오 ID 설정
        int prevVideoNum = videoService.getPrevVideoId(videoNum, userNum);
        int nextVideoNum = videoService.getNextVideoId(videoNum, userNum);

        model.addAttribute("prevVideoNum", prevVideoNum);
        model.addAttribute("nextVideoNum", nextVideoNum);

        return "video"; // video.html로 이동
    }

    @GetMapping("/bestvideos/video/{videoNum}")
    public String getBestVideosVideoPage(@PathVariable("videoNum") int videoNum,
                                         @RequestParam(value = "weekOffset", defaultValue = "0") int weekOffset,
                                         @RequestParam(value = "topicNum", required = false) Integer topicNum, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";  // 로그인 페이지로 리다이렉트
        }
        String currentUsername = authentication.getName();
        Optional<User> currentUserOptional = userService.findByUserId2(currentUsername);
        Optional<Video> videoOptional = videoService.findById(videoNum);

        if (!currentUserOptional.isPresent()) {
            return "redirect:/"; // 사용자가 없으면 홈으로 리다이렉트
        }
        if (!videoOptional.isPresent()) {
            return "error/404"; // 비디오가 없을 경우 404 페이지로 이동
        }

        User currentUser = currentUserOptional.get();
        Integer currentUserNum = currentUser.getUserNum();
        Video video = videoOptional.get();
        User user = video.getUser();

        String userName = user.getUserName();
        String schoolName = user.getSchool().getSchoolName();
        String profilePictureUrl = user.getProfilePictureUrl();

        model.addAttribute("userName", userName);
        model.addAttribute("schoolName", schoolName);
        model.addAttribute("profilePictureUrl", profilePictureUrl);
        model.addAttribute("userNum", user.getUserNum());
        model.addAttribute("videoUrl", video.getVideoUrl());
        model.addAttribute("videoTitle", video.getTitle());
        model.addAttribute("videoUser", video.getUser());
        model.addAttribute("weekOffset", weekOffset);

        boolean isFollowing = followService.isFollowing(currentUserNum, user.getUserNum());
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("currentUserNum", currentUserNum);

        List<User> followingUsers = followService.getFollowingUsers(user.getUserNum());
        List<User> followerUsers = followService.getFollowerUsers(user.getUserNum());
        model.addAttribute("followingUsers", followingUsers);
        model.addAttribute("followerUsers", followerUsers);

        boolean isLiked = videoLikeService.isLikedByUser(videoNum, currentUserNum);
        model.addAttribute("isLiked", isLiked);
        int likeCount = videoLikeService.countLikesByVideoId(videoNum);
        model.addAttribute("likeCount", likeCount);

        List<Comment> comments = commentService.findByVideoNum(videoNum);
        for (Comment comment : comments) {
            boolean isCommentLiked = commentLikeService.isCommentLikedByUser(comment.getCommentNum(), currentUserNum);
            comment.setLiked(isCommentLiked);
            boolean hasReplies = commentService.hasReplies(comment.getCommentNum());
            comment.setHasReplies(hasReplies);
        }
        commentService.setLikeCountsForComments(comments);
        model.addAttribute("comments", comments);
        int commentCount = commentService.countCommentsByVideoNum(videoNum);
        model.addAttribute("commentCount", commentCount);

        // 베스트 비디오 목록을 가져와서 이전 및 다음 비디오를 설정
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate startOfWeek = monday.minusWeeks(weekOffset + 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        LocalDateTime startDateTime = startOfWeek.atStartOfDay();
        LocalDateTime endDateTime = endOfWeek.atTime(LocalTime.MAX);

        List<Object[]> topVideos = topicNum == null ? videoService.findTopVideosForWeek(startDateTime, endDateTime)
                : videoService.findTopVideosForWeekAndTopic(startDateTime, endDateTime, topicNum);
        int currentIndex = -1;
        for (int i = 0; i < topVideos.size(); i++) {
            if (((Video) topVideos.get(i)[0]).getVideoNum() == videoNum) {
                currentIndex = i;
                break;
            }
        }

        int prevVideoNum = currentIndex > 0 ? ((Video) topVideos.get(currentIndex - 1)[0]).getVideoNum() : 0;
        int nextVideoNum = currentIndex < topVideos.size() - 1 ? ((Video) topVideos.get(currentIndex + 1)[0]).getVideoNum() : 0;

        model.addAttribute("prevVideoNum", prevVideoNum);
        model.addAttribute("nextVideoNum", nextVideoNum);
        model.addAttribute("topicNum", topicNum); // 추가

        return "bestvideos_video"; // bestvideos_video.html로 이동
    }
    
    @GetMapping("/home_video/{videoNum}")
    public String getVideoPage2(@PathVariable("videoNum") int videoNum, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";  // 로그인 페이지로 리다이렉트
        }
        String currentUsername = authentication.getName();  // 로그인한 사용자의 이름 가져오기
        Optional<User> currentUserOptional = userService.findByUserId2(currentUsername);
        Optional<Video> videoOptional = videoService.findById(videoNum);
        
        if (!currentUserOptional.isPresent()) {
            return "redirect:/"; // 사용자가 없으면 홈으로 리다이렉트
        }
        if (!videoOptional.isPresent()) {
            return "error/404"; // 비디오가 없을 경우 404 페이지로 이동
        }
        
        User currentUser = currentUserOptional.get();
        Integer currentUserNum = currentUser.getUserNum(); // 현재 로그인한 사용자의 userNum
        Video video = videoOptional.get();

        Integer userNum = videoService.getUserNumByVideoNum(videoNum); // userNum 가져오기

        if (userNum == null) {
            return "redirect:/"; // 사용자를 찾을 수 없으면 홈으로 리다이렉트
        }

        User user = userService.findByUserNum(userNum);
        if (user == null) {
            return "redirect:/"; // 사용자를 찾을 수 없으면 홈으로 리다이렉트
        }
        
        String userName = user.getUserName();
        String schoolName = user.getSchool().getSchoolName();
        String profilePictureUrl = user.getProfilePictureUrl(); // 상대방의 프로필 사진 URL을 가져옴
        
        model.addAttribute("userName", userName);
        model.addAttribute("schoolName", schoolName);
        model.addAttribute("profilePictureUrl", profilePictureUrl); // 프로필 사진 URL 추가
        model.addAttribute("userNum", userNum); // 조회된 사용자의 userNum
        model.addAttribute("videoUrl", video.getVideoUrl());
        model.addAttribute("videoTitle", video.getTitle());
        model.addAttribute("videoUser", video.getUser());
        
        // 팔로우 상태 추가
        boolean isFollowing = followService.isFollowing(currentUserNum, userNum);
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("currentUserNum", currentUserNum); // 현재 로그인한 사용자의 userNum 추가

        List<User> followingUsers = followService.getFollowingUsers(userNum);
        List<User> followerUsers = followService.getFollowerUsers(userNum);

        model.addAttribute("followingUsers", followingUsers);
        model.addAttribute("followerUsers", followerUsers);

        // 좋아요 상태 확인
        boolean isLiked = videoLikeService.isLikedByUser(videoNum, currentUserNum);
        model.addAttribute("isLiked", isLiked);

        // 좋아요 수 카운트
        int likeCount = videoLikeService.countLikesByVideoId(videoNum);
        model.addAttribute("likeCount", likeCount);
        
        // 댓글 목록을 모델에 추가
        List<Comment> comments = commentService.findByVideoNum(videoNum);
        for (Comment comment : comments) {
            boolean isCommentLiked = commentLikeService.isCommentLikedByUser(comment.getCommentNum(), currentUserNum);
            comment.setLiked(isCommentLiked);

            // 댓글에 답글이 있는지 확인하여 모델에 추가
            boolean hasReplies = commentService.hasReplies(comment.getCommentNum());
            comment.setHasReplies(hasReplies);
        }
        commentService.setLikeCountsForComments(comments);
        model.addAttribute("comments", comments);
        
        // 댓글 수 추가
        int commentCount = commentService.countCommentsByVideoNum(videoNum);
        model.addAttribute("commentCount", commentCount);
        
        // 이전 및 다음 비디오 ID 설정
        int prevVideoNum = videoService.getPrevHomeVideoId(videoNum);
        int nextVideoNum = videoService.getNextHomeVideoId(videoNum);

        model.addAttribute("prevVideoNum", prevVideoNum);
        model.addAttribute("nextVideoNum", nextVideoNum);

        return "home_video"; // video.html로 이동
    }


    
    @PostMapping("/video/incrementViewCount")
    @ResponseBody
    public void incrementViewCount(@RequestBody Map<String, Integer> request) {
        int videoNum = request.get("videoNum");
        videoService.incrementViewCount(videoNum);
    }
}
