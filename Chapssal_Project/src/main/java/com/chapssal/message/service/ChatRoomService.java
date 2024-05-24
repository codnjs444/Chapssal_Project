package com.chapssal.message.service;

import com.chapssal.message.model.ChatRoom;
import com.chapssal.message.model.Message;
import com.chapssal.message.model.Participant;
import com.chapssal.message.repository.ChatRoomRepository;
import com.chapssal.message.repository.MessageRepository;
import com.chapssal.message.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatRoomService {
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private MessageRepository messageRepository;

    public List<ChatRoom> getAllChatRooms() {
        return chatRoomRepository.findAll();
    }

    public ChatRoom getChatRoomById(int roomNum) {
        return chatRoomRepository.findById(roomNum).orElse(null);
    }

    public ChatRoom createChatRoom(ChatRoom chatRoom) {
        chatRoom.setCreateDate(LocalDateTime.now());
        return chatRoomRepository.save(chatRoom);
    }

    public void deleteChatRoom(int roomNum) {
        chatRoomRepository.deleteById(roomNum);
    }

    public long getChatRoomCount() {
        return chatRoomRepository.count();
    }

    public List<Participant> getParticipantsByRoomNum(int roomNum) {
        return participantRepository.findByRoom_RoomNum(roomNum);
    }

    public List<Message> getMessagesByRoomNum(int roomNum) {
        return messageRepository.findByChatRoom_RoomNum(roomNum);
    }

    public Message addMessage(int roomNum, Message message) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomNum)
                .orElseThrow(() -> new IllegalArgumentException("Invalid room number"));
        message.setChatRoom(chatRoom);
        return messageRepository.save(message);
    }
}