package com.chapssal.message.repository;

import com.chapssal.message.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    @Query("SELECT COUNT(c) FROM ChatRoom c")
    long countAllChatRooms();

    @Query("SELECT c FROM ChatRoom c JOIN c.participants p WHERE p.user.userNum = :userNum")
    List<ChatRoom> findChatRoomsByUserNum(@Param("userNum") Integer userNum);

    @Query("SELECT COUNT(c) FROM ChatRoom c JOIN c.participants p WHERE p.user.userNum = :userNum")
    long countChatRoomsByUserNum(@Param("userNum") Integer userNum);


}