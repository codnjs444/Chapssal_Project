package com.chapssal.message.controller;

import com.chapssal.message.model.ChatRoom;
import com.chapssal.message.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ChatRoomService chatRoomService;

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
        long chatRoomCount = chatRoomService.getChatRoomCount();
        List<ChatRoom> chatRooms = chatRoomService.getAllChatRooms();
        model.addAttribute("chatRoomCount", chatRoomCount);
        model.addAttribute("chatRooms", chatRooms);
        return "message/home";
    }
}
