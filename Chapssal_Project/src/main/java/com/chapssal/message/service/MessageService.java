package com.chapssal.message.service;

import com.chapssal.message.model.Message;
import com.chapssal.message.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Message addMessage(Message message) {
        message.setSendDate(LocalDateTime.now());
        return messageRepository.save(message);
    }
}