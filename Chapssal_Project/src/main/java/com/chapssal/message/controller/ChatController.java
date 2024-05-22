package com.chapssal.message.controller;

import com.chapssal.message.model.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message send(Message message) {
        message.setSendDate(LocalDateTime.now());
        return message;
    }
}