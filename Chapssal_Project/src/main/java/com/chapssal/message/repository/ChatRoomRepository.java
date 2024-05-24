package com.chapssal.message.repository;

import com.chapssal.message.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    @Query("SELECT COUNT(c) FROM ChatRoom c")
    long countAllChatRooms();
}