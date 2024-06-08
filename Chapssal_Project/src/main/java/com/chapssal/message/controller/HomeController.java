package com.chapssal.message.controller;

import com.chapssal.message.model.ChatRoom;
import com.chapssal.message.service.ChatRoomService;
import com.chapssal.user.User;
import com.chapssal.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private  UserService userService;

    @GetMapping("/message")
    public String message() {
        return "message/message";
    }

    @GetMapping("/message2")
    public String message2() {
        return "message/message2";
    }

    @GetMapping("/message3")
    public String message3() {
        return "message/message3";
    }

    @GetMapping("message/home")
    public String messageHome(Model model) {

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
        String currentUserName = currentUser.getUserName();
        String currentUserId = currentUser.getUserId();
        String profilePictureUrl = currentUser.getProfilePictureUrl(); // 현재 로그인한 사용자의 프로필 이미지 URL

        model.addAttribute("currentUserNum", currentUserNum); // 현재 로그인한 사용자의 userNum 추가
        model.addAttribute("currentUserName", currentUserName); // 이름
        model.addAttribute("currentUserId", currentUserId); // 이름
        model.addAttribute("profilePictureUrl", profilePictureUrl); // 프로필 이미지 URL 추가

        List<ChatRoom> chatRooms = chatRoomService.getChatRoomsByUserNumWithParticipants(currentUserNum);
        model.addAttribute("chatRooms", chatRooms);
        return "message/home";
    }

    @GetMapping("/search")
    public String search() {
        return "message/search";
    }
}
