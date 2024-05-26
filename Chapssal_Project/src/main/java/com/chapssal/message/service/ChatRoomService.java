package com.chapssal.message.service;

import com.chapssal.message.model.ChatRoom;
import com.chapssal.message.model.Message;
import com.chapssal.message.model.Participant;
import com.chapssal.message.repository.ChatRoomRepository;
import com.chapssal.message.repository.MessageRepository;
import com.chapssal.message.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chapssal.user.User;
import com.chapssal.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ChatRoom> getAllChatRooms() {
        return chatRoomRepository.findAll();
    }

    public List<ChatRoom> getChatRoomsByUserNum(Integer userNum) {
        return chatRoomRepository.findChatRoomsByUserNum(userNum);
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

    public long getChatRoomCountByUserNum(Integer userNum) {
        return chatRoomRepository.countChatRoomsByUserNum(userNum);
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

//    public List<ChatRoom> getChatRoomsByUserNumWithParticipants(Integer currentUserNum) {
//        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByUserNum(currentUserNum);
//        for (ChatRoom chatRoom : chatRooms) {
//            List<String> otherParticipantsNames = chatRoom.getParticipants().stream()
//                    .map(Participant::getUser)
//                    .filter(user -> !user.getUserNum().equals(currentUserNum))
//                    .map(User::getUserName)
//                    .collect(Collectors.toList());
//            chatRoom.setOtherParticipantsNames(String.join(", ", otherParticipantsNames));
//        }
//        return chatRooms;
//    }

    public List<ChatRoom> getChatRoomsByUserNumWithParticipants(Integer currentUserNum) {
        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByUserNum(currentUserNum);
        for (ChatRoom chatRoom : chatRooms) {
            List<User> otherParticipants = userRepository.findOtherParticipants(chatRoom.getRoomNum(), currentUserNum);
            chatRoom.setOtherParticipants(otherParticipants);

            // 최근 메시지 가져오기
            Optional<Message> recentMessageOptional = messageRepository.findTopByChatRoomOrderBySendDateDesc(chatRoom);
            if (recentMessageOptional.isPresent()) {
                Message recentMessage = recentMessageOptional.get();
                chatRoom.setRecentMessage(recentMessage.getText());
                chatRoom.setRecentMessageDate(recentMessage.getSendDate());
            }
        }
        return chatRooms;
    }

}