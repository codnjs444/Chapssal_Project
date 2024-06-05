package com.chapssal.user;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.chapssal.award.Award;
import com.chapssal.award.AwardService;
import com.chapssal.follow.FollowService;
import com.chapssal.school.School;
import com.chapssal.school.SchoolRepository;
import com.chapssal.topic.SelectedTopicService;
import com.chapssal.video.S3Service;
import com.chapssal.video.Video;
import com.chapssal.video.VideoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private final UserService userService;
	
    @Autowired
    private FollowService followService;

    @Autowired
    private final VideoService videoService;
    
    @Autowired
    private final SchoolRepository schoolRepository;
    
    @Autowired
    private AwardService awardService;
    
    @Autowired
    private SelectedTopicService selectedTopicService;
    
    @Autowired
    private S3Service s3Service;
    private static final String TEMP_FOLDER = System.getProperty("java.io.tmpdir");
    
    @ModelAttribute("topicsByVoteCount")
    public List<Object[]> topicsByVoteCount() {
        return selectedTopicService.findTopicsByVoteCount();
    }
    
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("userCreateForm", new UserCreateForm());
        return "signup_form";
    }

	
    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }
        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 비밀번호가 일치하지 않습니다.");
            return "signup_form";
        }

        // 학교 코드가 존재하는지 확인
        School school = schoolRepository.findBySchoolCode(userCreateForm.getSchoolCode()).orElse(null);
        if(school == null) {
            bindingResult.rejectValue("schoolCode", "schoolCodeNotFound", "유효한 학교 코드가 아닙니다.");
            return "signup_form";
        }

        try {
            userService.create(
                    userCreateForm.getUserId(),
                    userCreateForm.getPassword1(),
                    school,  // 유저 엔티티에 학교 번호 저장
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
        } catch (DuplicateUserIdException e) {
            bindingResult.rejectValue("userId", "duplicateUserId", e.getMessage());
            return "signup_form";
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "회원가입이 성공하셨습니다.");
        return "redirect:/";
    }
	
    @GetMapping("/login")
    public String login() {
        return "login_form";
    }
    
 // 이 부분부터 소셜로그인 학교 코드 입력에 대한 부분

    @GetMapping("/enterSchoolCode")
    public String enterSchoolCode(Model model) {
        model.addAttribute("schoolCodeForm", new SchoolCodeForm());
        return "schoolCode_form";
    }

    @PostMapping("/enterSchoolCode")
    public String enterSchoolCode(@Valid SchoolCodeForm schoolCodeForm, BindingResult bindingResult, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "schoolCode_form";
        }

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String userId = oAuth2User.getAttribute("id").toString();
        User user = userService.findByUserId(userId);

        School school = schoolRepository.findBySchoolCode(schoolCodeForm.getSchoolCode()).orElse(null);
        if (school == null) {
            bindingResult.rejectValue("schoolCode", "schoolCodeNotFound", "유효한 학교 코드가 아닙니다.");
            return "schoolCode_form";
        }

        user.setSchool(school);
        userService.save(user);

        redirectAttributes.addFlashAttribute("successMessage", "학교 코드가 성공적으로 등록되었습니다.");
        return "redirect:/";
    }
    
    @GetMapping("/profile")
    public String getUserProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";  // 로그인 페이지로 리다이렉트
        }
        
        String username = authentication.getName();  // 로그인한 사용자의 이름 가져오기
        User user = userService.getUser(username);
        String schoolName = userService.getSchoolNameByUserId(username);
        String userName = userService.getUserNameByUserId(username);
        String bio = userService.getUserBioByUserId(username);
        String profilePictureUrl = user.getProfilePictureUrl();
        
        model.addAttribute("schoolName", schoolName);
        model.addAttribute("userName", userName);
        model.addAttribute("bio", bio);
        model.addAttribute("profilePictureUrl", profilePictureUrl); // 프로필 사진 URL 추가
        
        Integer userNum = userService.getUserNumByUserId(username);
        int followingCount = followService.countFollowingByUserNum(userNum);
        int followerCount = followService.countFollowerByUserNum(userNum);
        int videoCount = videoService.countVideosByUserNum(userNum);  // 게시글 수 추가
        
        model.addAttribute("followingCount", followingCount);
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("videoCount", videoCount); // 게시글 수 모델에 추가
        
        List<User> followingUsers = followService.getFollowingUsers(userNum);
        List<User> followerUsers = followService.getFollowerUsers(userNum);
        
        model.addAttribute("followingUsers", followingUsers);
        model.addAttribute("followerUsers", followerUsers);
        
        model.addAttribute("currentUserNum", userNum); // 현재 사용자의 userNum 추가
        
        // 수상 정보 추가
        List<Award> awards = awardService.getAwardsByUserNum(userNum);
        model.addAttribute("awards", awards);
        
        List<Video> userVideos = videoService.getVideosByUserNum(userNum);
        model.addAttribute("userVideos", userVideos);
        
        return "profile";
    }
    
    @PostMapping("/updateProfile")
    public String updateProfile(@RequestParam("userName") String userName,
                                @RequestParam("bio") String bio,
                                @RequestParam("profilePicInput") MultipartFile file,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {

        User user = userService.getUser(authentication.getName());

        if (!file.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path tempFilePath = Paths.get(TEMP_FOLDER, fileName);
                Files.write(tempFilePath, file.getBytes());

                File tempFile = tempFilePath.toFile();
                String s3Key = "profile-pictures/" + fileName;
                s3Service.uploadFile(s3Key, tempFile);

                String profilePictureUrl = s3Service.getFileUrl(s3Key);
                user.setProfilePictureUrl(profilePictureUrl);

                Files.delete(tempFilePath);

            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("message", "File upload failed: " + e.getMessage());
                return "redirect:/";
            }
        }

        user.setUserName(userName);
        user.setBio(bio);
        userService.updateUser(user);

        redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");
        return "redirect:/user/profile";
    }
    
    @PostMapping("/uploadProfilePicture")
    public ResponseEntity<Map<String, Object>> uploadProfilePicture(@RequestParam("profilePicInput") MultipartFile file, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        if (!file.isEmpty()) {
            try {
                User user = userService.getUser(authentication.getName());
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path tempFilePath = Paths.get(TEMP_FOLDER, fileName);
                Files.write(tempFilePath, file.getBytes());

                File tempFile = tempFilePath.toFile();
                String s3Key = "profile-pictures/" + fileName;
                s3Service.uploadFile(s3Key, tempFile);

                String profilePictureUrl = s3Service.getFileUrl(s3Key);
                user.setProfilePictureUrl(profilePictureUrl);
                userService.save(user);

                Files.delete(tempFilePath);

                response.put("success", true);
                response.put("profilePictureUrl", profilePictureUrl);
            } catch (IOException e) {
                e.printStackTrace();
                response.put("success", false);
                response.put("message", "File upload failed: " + e.getMessage());
            }
        } else {
            response.put("success", false);
            response.put("message", "No file selected");
        }

        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/profile/{userNum}")
    public String getUserProfile(@PathVariable("userNum") Integer userNum, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";  // 로그인 페이지로 리다이렉트
        }

        // 현재 로그인한 사용자의 정보를 가져옴
        String currentUsername = authentication.getName();  // 로그인한 사용자의 이름 가져오기
        Optional<User> currentUserOptional = userService.findByUserId2(currentUsername);
        
        if (!currentUserOptional.isPresent()) {
            return "redirect:/"; // 사용자가 없으면 홈으로 리다이렉트
        }
        
        User currentUser = currentUserOptional.get();
        Integer currentUserNum = currentUser.getUserNum(); // 현재 로그인한 사용자의 userNum
        
        // 프로필 페이지의 사용자 정보를 가져옴
        User user = userService.findByUserNum(userNum);
        if (user == null) {
            return "redirect:/"; // 사용자를 찾을 수 없으면 홈으로 리다이렉트
        }
        
        String userName = user.getUserName();
        String schoolName = user.getSchool().getSchoolName();
        String bio = user.getBio();
        String profilePictureUrl = user.getProfilePictureUrl(); // 상대방의 프로필 사진 URL을 가져옴
        
        model.addAttribute("userName", userName);
        model.addAttribute("schoolName", schoolName);
        model.addAttribute("bio", bio);
        model.addAttribute("profilePictureUrl", profilePictureUrl); // 프로필 사진 URL 추가
        model.addAttribute("userNum", userNum); // 조회된 사용자의 userNum
        
        int followingCount = followService.countFollowingByUserNum(userNum);
        int followerCount = followService.countFollowerByUserNum(userNum);
        int videoCount = videoService.countVideosByUserNum(userNum); // 게시글 수 계산
        
        model.addAttribute("followingCount", followingCount);
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("videoCount", videoCount); // 게시글 수 모델에 추가
        
        List<User> followingUsers = followService.getFollowingUsers(userNum);
        List<User> followerUsers = followService.getFollowerUsers(userNum);
        
        boolean isFollowing = followService.isFollowing(currentUserNum, userNum);
        model.addAttribute("isFollowing", isFollowing);
        
        model.addAttribute("followingUsers", followingUsers);
        model.addAttribute("followerUsers", followerUsers);
        model.addAttribute("currentUserNum", currentUserNum); // 현재 로그인한 사용자의 userNum 추가
        
        // 수상 정보 추가
        List<Award> awards = awardService.getAwardsByUserNum(userNum);
        model.addAttribute("awards", awards);
        
        // 상대방의 동영상 정보 추가
        List<Video> userVideos = videoService.getVideosByUserNum(userNum);
        model.addAttribute("userVideos", userVideos);

        return "user_profile";
    }

    @PostMapping("/resetProfilePicture")
    @ResponseBody
    public Map<String, Object> resetProfilePicture(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        try {
            User user = userService.getUser(authentication.getName());
            user.setProfilePictureUrl(null); // 기본 이미지를 사용하기 위해 URL을 null로 설정
            userService.save(user);
            
            response.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
        }

        return response;
    }

    @GetMapping("/test")
    public String testPage() {
        return "test";
    }
    
    @GetMapping("/suggestions")
    @ResponseBody
    public List<UserDTO> getUserSuggestions(@RequestParam(name = "query") String query) {
        return userService.getUserSuggestions(query).stream()
                .map(user -> new UserDTO(user.getUserName()))
                .collect(Collectors.toList());
    }

    static class UserDTO {
        private String userName;

        public UserDTO(String userName) {
            this.userName = userName;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
