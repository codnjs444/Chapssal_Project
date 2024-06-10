package com.chapssal.message.controller;

import com.chapssal.message.model.Message;
import com.chapssal.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageController messageController;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message send(Message message) {
        message.setSendDate(LocalDateTime.now());

        // 메시지를 데이터베이스에 저장
        Message savedMessage = messageService.addMessage(message);

        // 수신자에게 SSE 이벤트 전송
        messageController.sendSseEventToUser(savedMessage.getReceiver(), "새로운 메시지가 도착했습니다.");

        return savedMessage;
    }
}