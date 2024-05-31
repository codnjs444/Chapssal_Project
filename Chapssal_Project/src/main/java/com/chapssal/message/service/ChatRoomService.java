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
        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByUserNumAndNotLeft(currentUserNum);
        for (ChatRoom chatRoom : chatRooms) {
            // 모든 참가자를 가져오되, 나간 참가자도 포함
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
        // 최근 메시지 날짜 기준으로 정렬
        chatRooms.sort((cr1, cr2) -> {
            if (cr1.getRecentMessageDate() == null && cr2.getRecentMessageDate() == null) {
                return 0;
            } else if (cr1.getRecentMessageDate() == null) {
                return 1;
            } else if (cr2.getRecentMessageDate() == null) {
                return -1;
            } else {
                return cr2.getRecentMessageDate().compareTo(cr1.getRecentMessageDate());
            }
        });
        return chatRooms;
    }



    public Optional<ChatRoom> findChatRoomByParticipants(int userNum1, int userNum2) {
        return Optional.ofNullable(chatRoomRepository.findChatRoomByParticipants(userNum1, userNum2));
    }

    public ChatRoom createOrJoinChatRoom(List<Integer> participantNums) {
        int userNum1 = participantNums.get(0);
        int userNum2 = participantNums.get(1);

        Optional<ChatRoom> existingChatRoom = Optional.ofNullable(chatRoomRepository.findChatRoomByParticipants(userNum1, userNum2));
        if (existingChatRoom.isPresent()) {
            ChatRoom chatRoom = existingChatRoom.get();
            Participant participant = participantRepository.findByRoomAndUser(chatRoom, userRepository.findById(userNum1).orElseThrow(() -> new IllegalArgumentException("Invalid user number: " + userNum1))).orElseThrow(() -> new IllegalArgumentException("Participant not found"));
            if (participant.getIsLeave()) {
                participant.setIsLeave(false);
                participantRepository.save(participant);
            }
            return chatRoom;
        } else {
            ChatRoom newChatRoom = new ChatRoom();
            newChatRoom.setCreateDate(LocalDateTime.now());
            ChatRoom savedChatRoom = chatRoomRepository.save(newChatRoom);

            for (Integer participantNum : participantNums) {
                User user = userRepository.findById(participantNum)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid user number: " + participantNum));
                Participant participant = new Participant();
                participant.setRoom(savedChatRoom);
                participant.setUser(user);
                participant.setJoinDate(LocalDateTime.now());
                participant.setIsLeave(false);
                participantRepository.save(participant);
            }

            return savedChatRoom;
        }
    }

    public void leaveChatRoom(Long roomNum, Long userNum) {
        Participant participant = participantRepository.findByRoomAndUser(new ChatRoom(Math.toIntExact(roomNum)), userRepository.findById(Math.toIntExact(userNum)).orElseThrow(() -> new IllegalArgumentException("Invalid user number: " + userNum))).orElseThrow(() -> new IllegalArgumentException("Participant not found"));
        participant.setIsLeave(true);
        participantRepository.save(participant);
    }

    public void updateParticipantIsLeaveToFalse(int roomNum, int userNum) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomNum)
                .orElseThrow(() -> new IllegalArgumentException("Invalid room number"));
        User user = userRepository.findById(userNum)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user number"));

        Participant participant = participantRepository.findByRoomAndUser(chatRoom, user)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));
        participant.setIsLeave(false);
        participant.setJoinDate(LocalDateTime.now());
        participantRepository.save(participant);
    }

}
