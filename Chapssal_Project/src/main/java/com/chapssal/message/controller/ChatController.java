package com.chapssal.message.controller;

import com.chapssal.message.model.Message;
import com.chapssal.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    @Autowired
    private MessageService messageService;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message send(Message message) {
        message.setSendDate(LocalDateTime.now());
        // 메시지를 데이터베이스에 저장
        return messageService.addMessage(message);
    }

    @SendToUser("/queue/messages")
    public String processMessageFromClient(String message) {
        // 처리 로직을 여기에 추가
        return message; // 클라이언트로 보내는 메시지
    }

}