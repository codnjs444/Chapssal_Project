package com.chapssal.message.service;

import com.chapssal.message.model.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDTO {
    private int roomNum;
    private String title;
    private String recentMessage;
    private LocalDateTime recentMessageDate;
    private List<UserDTO> participants; // 추가된 필드

    public ChatRoomDTO(ChatRoom chatRoom) {
        this.roomNum = chatRoom.getRoomNum();
        this.title = chatRoom.getTitle();
        this.recentMessage = chatRoom.getRecentMessage();
        this.recentMessageDate = chatRoom.getRecentMessageDate();
    }
}