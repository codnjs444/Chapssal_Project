package com.chapssal.message.controller;

import com.chapssal.message.model.ChatRoom;
import com.chapssal.message.model.Message;
import com.chapssal.message.model.Participant;
import com.chapssal.message.repository.ParticipantRepository;
import com.chapssal.message.service.*;
import com.chapssal.user.User;
import com.chapssal.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
public class ChatRoomController {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/rooms")
    public List<ChatRoom> getAllChatRooms() {
        return chatRoomService.getAllChatRooms();
    }

    @GetMapping("/rooms/{roomNum}")
    public ChatRoom getChatRoomById(@PathVariable("roomNum") int roomNum) {
        return chatRoomService.getChatRoomById(roomNum);
    }

    @PostMapping("/rooms")
    public ChatRoom createChatRoom(@RequestBody ChatRoom chatRoom) {
        return chatRoomService.createChatRoom(chatRoom);
    }

    @DeleteMapping("/rooms/{roomNum}")
    public void deleteChatRoom(@PathVariable("roomNum") int roomNum) {
        chatRoomService.deleteChatRoom(roomNum);
    }

    @GetMapping("/chatroom/count")
    public long getChatRoomCount() {
        return chatRoomService.getChatRoomCount();
    }

    @GetMapping("/rooms/{roomNum}/participants")
    public List<Participant> getParticipantsByRoomNum(@PathVariable("roomNum") int roomNum) {
        return chatRoomService.getParticipantsByRoomNum(roomNum);
    }

    @GetMapping("/rooms/{roomNum}/messages")
    public ResponseEntity<List<Message>> getMessages(
            @PathVariable("roomNum") Integer roomNum,
            @RequestParam(name = "oldestMessageId", required = false) Integer oldestMessageId,
            @RequestParam(name = "limit", defaultValue = "30") int limit) {
        List<Message> messages = messageService.getMessagesBeforeId(roomNum, oldestMessageId, limit);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/rooms/{roomNum}/messages")
    public Message createMessage(@PathVariable("roomNum") int roomNum, @RequestBody Message message) {
        ChatRoom chatRoom = chatRoomService.getChatRoomById(roomNum);
        message.setChatRoom(chatRoom);
        return messageService.saveMessage(message);
    }

    @GetMapping("/rooms/{roomNum}/unreadCount")
    public ResponseEntity<Long> getUnreadMessageCount(
            @PathVariable("roomNum") int roomNum,
            @RequestParam(name = "userNum") int userNum) {
        long unreadCount = messageService.countUnreadMessages(roomNum, userNum);
        return ResponseEntity.ok(unreadCount);
    }

    @PostMapping("/rooms/{roomNum}/markAsRead")
    public ResponseEntity<Void> markMessagesAsRead(@PathVariable("roomNum") int roomNum, @RequestParam(name = "userNum") int userNum) {
        messageService.markMessagesAsRead(roomNum, userNum);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/rooms/{roomNum}/participants/{userNum}")
    public ResponseEntity<Void> leaveChatRoom(@PathVariable("roomNum") Long roomNum, @PathVariable("userNum") Long userNum) {
        chatRoomService.leaveChatRoom(roomNum, userNum);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rooms/searchUsers")
    public List<User> searchUsers(@RequestParam(name = "query") String query) {
        boolean isEnglish = query.matches("^[a-zA-Z0-9]*$");
        return participantService.searchUsers(query, isEnglish);
    }

    @GetMapping("/rooms/check")
    public ResponseEntity<ChatRoom> checkExistingChatRoom(@RequestParam(name = "userNum1") int userNum1, @RequestParam(name = "userNum2") int userNum2) {
        Optional<ChatRoom> existingChatRoom = chatRoomService.findChatRoomByParticipants(userNum1, userNum2);
        if (existingChatRoom.isPresent()) {
            ChatRoom chatRoom = existingChatRoom.get();
            List<Participant> participants = participantRepository.findByRoomAndIsLeave(chatRoom, false);
            boolean isCurrentUserInRoom = participants.stream().anyMatch(p -> p.getUser().getUserNum() == userNum1);
            if (isCurrentUserInRoom) {
                return ResponseEntity.ok(chatRoom);
            } else {
                return ResponseEntity.status(202).body(chatRoom); // isLeave가 true인 경우
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("rooms/create")
    public ResponseEntity<ChatRoomDTO> createChatRoom(@RequestBody Map<String, List<Integer>> participantsMap) {
        List<Integer> participants = participantsMap.get("participants");
        if (participants == null || participants.size() != 2) {
            return ResponseEntity.badRequest().build();
        }

        ChatRoom newChatRoom = chatRoomService.createOrJoinChatRoom(participants);

        // WebSocket 메시지 전송
        participants.forEach(participant ->
                messagingTemplate.convertAndSend("/topic/chatRoomUpdate/" + participant, new ChatRoomDTO(newChatRoom))
        );

        // 새로 생성된 채팅방 정보 반환
        return ResponseEntity.ok(new ChatRoomDTO(newChatRoom));
    }

    @PostMapping("/rooms/{roomNum}/updateIsLeaveToFalse")
    public ResponseEntity<Void> updateIsLeaveToFalse(@PathVariable("roomNum") int roomNum, @RequestBody Map<String, Integer> request) {
        int userNum = request.get("userNum");
        chatRoomService.updateParticipantIsLeaveToFalse(roomNum, userNum);
        return ResponseEntity.ok().build();
    }

    @GetMapping("rooms/user/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") int id) {
        User user = userRepository.findByUserNum(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        UserDTO userDTO = new UserDTO(user);
        return ResponseEntity.ok(userDTO);
    }

}
