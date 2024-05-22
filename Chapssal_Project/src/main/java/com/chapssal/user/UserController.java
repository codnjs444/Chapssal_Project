package com.chapssal.user;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.chapssal.follow.FollowService;
import com.chapssal.school.School;
import com.chapssal.school.SchoolRepository;

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
    private final SchoolRepository schoolRepository;
    
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
                    school,  // 유저 엔티티에 학교 객체 저장
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
        String schoolName = userService.getSchoolNameByUserId(username);
        String userName = userService.getUserNameByUserId(username);
        String bio = userService.getUserBioByUserId(username);

        model.addAttribute("schoolName", schoolName);
        model.addAttribute("userName", userName);
        model.addAttribute("bio", bio);

        Integer userNum = userService.getUserNumByUserId(username);
        int followingCount = followService.countFollowingByUserNum(userNum);
        int followerCount = followService.countFollowerByUserNum(userNum);

        model.addAttribute("followingCount", followingCount);
        model.addAttribute("followerCount", followerCount);

        List<User> followingUsers = followService.getFollowingUsers(userNum);
        List<User> followerUsers = followService.getFollowerUsers(userNum);

        model.addAttribute("followingUsers", followingUsers);
        model.addAttribute("followerUsers", followerUsers);

        return "profile";
    }


    
    @PostMapping("/updateProfile")
    public String updateProfile(@RequestParam("userName") String newUserName, @RequestParam("bio") String bio, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";  // 로그인 페이지로 리다이렉트
        }

        String username = authentication.getName();  // 로그인한 사용자의 이름 가져오기

        try {
            userService.updateUserName(username, newUserName, bio);
            redirectAttributes.addFlashAttribute("successMessage", "프로필이 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "프로필 업데이트에 실패했습니다.");
            return "redirect:/user/profile";
        }

        return "redirect:/user/profile";
    }
    
    @GetMapping("/profile/{userNum}")
    public String getUserProfile(@PathVariable("userNum") Integer userNum, Model model) {
        User user = userService.findByUserNum(userNum);
        if (user == null) {
            return "redirect:/"; // 사용자를 찾을 수 없으면 홈으로 리다이렉트
        }

        String userName = user.getUserName();
        String schoolName = user.getSchool().getSchoolName();
        String bio = user.getBio();

        model.addAttribute("userName", userName);
        model.addAttribute("schoolName", schoolName);
        model.addAttribute("bio", bio);
        model.addAttribute("userNum", userNum);

        int followingCount = followService.countFollowingByUserNum(userNum);
        int followerCount = followService.countFollowerByUserNum(userNum);

        model.addAttribute("followingCount", followingCount);
        model.addAttribute("followerCount", followerCount);
        
        List<User> followingUsers = followService.getFollowingUsers(userNum);
        List<User> followerUsers = followService.getFollowerUsers(userNum);

        model.addAttribute("followingUsers", followingUsers);
        model.addAttribute("followerUsers", followerUsers);
        return "user_profile";
    }



}
