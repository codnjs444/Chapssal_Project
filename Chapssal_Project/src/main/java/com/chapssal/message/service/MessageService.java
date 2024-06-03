package com.chapssal.message.service;

import com.chapssal.message.model.ChatRoom;
import com.chapssal.message.model.Message;
import com.chapssal.message.model.Participant;
import com.chapssal.message.repository.ChatRoomRepository;
import com.chapssal.message.repository.MessageRepository;
import com.chapssal.message.repository.ParticipantRepository;
import com.chapssal.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepository userRepository;


    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @Transactional
    public Message addMessage(Message message) {
        ChatRoom chatRoom = chatRoomRepository.findById(message.getRoomNum())
                .orElseThrow(() -> new IllegalArgumentException("Invalid room number"));
        message.setChatRoom(chatRoom);
        message.setSendDate(LocalDateTime.now());
        message.setMessageType((byte) 1); // or appropriate value
        message.setIsRead((byte) 0); // or appropriate value

        // 참여자들의 isLeave 상태를 false로 업데이트
        List<Participant> participants = participantRepository.findByRoom(chatRoom);
        for (Participant participant : participants) {
            if (participant.getIsLeave() != null && participant.getIsLeave()) {
                participant.setIsLeave(false);
                participant.setJoinDate(LocalDateTime.now());
                participantRepository.save(participant);

                // WebSocket 메시지 발송
                ChatRoomDTO chatRoomDTO = new ChatRoomDTO(chatRoom);
                messagingTemplate.convertAndSend("/topic/chatRoomUpdate/" + participant.getUser().getUserNum(), chatRoomDTO);
            }
        }

        return messageRepository.save(message);
    }

    public List<Message> getMessagesByRoomNum(int roomNum) {
        return messageRepository.findByChatRoom_RoomNum(roomNum);
    }


    public List<Message> getMessagesBeforeId(int roomNum, Integer oldestMessageId, int limit) {
        if (oldestMessageId == null) {
            // 만약 oldestMessageId가 null이면, 가장 최신 메시지들을 가져옴
            return messageRepository.findByChatRoom_RoomNumOrderBySendDateDesc(roomNum, PageRequest.of(0, limit));
        } else {
            // 이전 메시지들을 가져옴
            return messageRepository.findByChatRoom_RoomNumAndMessageNumLessThanOrderBySendDateDesc(roomNum, oldestMessageId, PageRequest.of(0, limit));
        }
    }

    public Message saveMessage(Message message) {
            return addMessage(message);
    }

    public long countUnreadMessages(int roomNum, int currentUserNum) {
        return messageRepository.countByChatRoom_RoomNumAndReceiver_UserNumAndIsRead(roomNum, currentUserNum);
    }

    public void markMessagesAsRead(int roomNum, int currentUserNum) {
        messageRepository.updateIsReadByChatRoom_RoomNumAndReceiver_UserNum(roomNum, currentUserNum);
    }

    public int countUnreadMessagesForUser(String username) {
        Integer userNum = userRepository.findUserNumByUserId(username);
        if (userNum == null) {
            return 0;
        }
        return messageRepository.countUnreadMessages2(userNum);
    }

}