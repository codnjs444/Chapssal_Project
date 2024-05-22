package com.chapssal.message.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/index.html";
    }

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

    @GetMapping("/home")
    public String home2() {
        return "message/home";
    }
}