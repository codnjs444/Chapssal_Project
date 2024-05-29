package com.chapssal.message.controller;

import com.chapssal.message.model.ChatRoom;
import com.chapssal.message.model.Message;
import com.chapssal.message.model.Participant;
import com.chapssal.user.User;
import com.chapssal.message.service.ChatRoomService;
import com.chapssal.message.service.MessageService;
import com.chapssal.message.service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatRoomController {
    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ParticipantService participantService;

    @GetMapping("/rooms")
    public List<ChatRoom> getAllChatRooms() {
        return chatRoomService.getAllChatRooms();
    }

    @GetMapping("/rooms/{roomNum}")
    public ChatRoom getChatRoomById(@PathVariable int roomNum) {
        return chatRoomService.getChatRoomById(roomNum);
    }

    @PostMapping("/rooms")
    public ChatRoom createChatRoom(@RequestBody ChatRoom chatRoom) {
        return chatRoomService.createChatRoom(chatRoom);
    }

    @DeleteMapping("/rooms/{roomNum}")
    public void deleteChatRoom(@PathVariable int roomNum) {
        chatRoomService.deleteChatRoom(roomNum);
    }

    @GetMapping("/chatroom/count")
    public long getChatRoomCount() {
        return chatRoomService.getChatRoomCount();
    }

    @GetMapping("/rooms/{roomNum}/participants")
    public List<Participant> getParticipantsByRoomNum(@PathVariable int roomNum) {
        return chatRoomService.getParticipantsByRoomNum(roomNum);
    }

    @GetMapping("/rooms/{roomNum}/messages")
    public ResponseEntity<List<Message>> getMessages(
            @PathVariable Integer roomNum,
            @RequestParam(required = false) Integer oldestMessageId,
            @RequestParam(defaultValue = "30") int limit) {
        List<Message> messages = messageService.getMessagesBeforeId(roomNum, oldestMessageId, limit);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/rooms/{roomNum}/messages")
    public Message createMessage(@PathVariable int roomNum, @RequestBody Message message) {
        ChatRoom chatRoom = chatRoomService.getChatRoomById(roomNum);
        message.setChatRoom(chatRoom);
        return messageService.saveMessage(message);
    }

    @GetMapping("/rooms/{roomNum}/unreadCount")
    public ResponseEntity<Long> getUnreadMessageCount(
            @PathVariable int roomNum,
            @RequestParam int userNum) {
        long unreadCount = messageService.countUnreadMessages(roomNum, userNum);
        return ResponseEntity.ok(unreadCount);
    }

    @PostMapping("/rooms/{roomNum}/markAsRead")
    public ResponseEntity<Void> markMessagesAsRead(@PathVariable int roomNum, @RequestParam int userNum) {
        messageService.markMessagesAsRead(roomNum, userNum);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/rooms/{roomNum}/participants/{userNum}")
    public ResponseEntity<Void> leaveChatRoom(@PathVariable Long roomNum, @PathVariable Long userNum) {
        chatRoomService.leaveChatRoom(roomNum, userNum);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rooms/searchUsers")
    public List<User> searchUsers(@RequestParam String query) {
        boolean isEnglish = query.matches("^[a-zA-Z0-9]*$");
        return participantService.searchUsers(query, isEnglish);
    }

}