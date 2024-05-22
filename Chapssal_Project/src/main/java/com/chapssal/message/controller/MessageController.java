package com.chapssal.message.controller;

import com.chapssal.message.model.Message;
import com.chapssal.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping
    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }

    @PostMapping
    public Message saveMessage(@RequestBody Message message) {
        return messageService.addMessage(message);
    }
}