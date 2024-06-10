package com.chapssal.message.repository;

import com.chapssal.message.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    @Query("SELECT COUNT(c) FROM ChatRoom c")
    long countAllChatRooms();

    @Query("SELECT c FROM ChatRoom c JOIN c.participants p WHERE p.user.userNum = :userNum")
    List<ChatRoom> findChatRoomsByUserNum(@Param("userNum") Integer userNum);

    @Query("SELECT COUNT(c) FROM ChatRoom c JOIN c.participants p WHERE p.user.userNum = :userNum")
    long countChatRoomsByUserNum(@Param("userNum") Integer userNum);

    @Query("SELECT cr FROM ChatRoom cr JOIN Participant p1 ON p1.room = cr JOIN Participant p2 ON p2.room = cr WHERE p1.user.userNum = :userNum1 AND p2.user.userNum = :userNum2 AND p1.user.userNum != p2.user.userNum")
    ChatRoom findChatRoomByParticipants(@Param("userNum1") int userNum1, @Param("userNum2") int userNum2);

    @Query("SELECT cr FROM ChatRoom cr JOIN cr.participants p WHERE p.user.userNum = :userNum AND p.isLeave = false")
    List<ChatRoom> findChatRoomsByUserNumAndNotLeft(@Param("userNum") Integer userNum);

    @Query("SELECT cr FROM ChatRoom cr JOIN Participant p1 ON p1.room = cr JOIN Participant p2 ON p2.room = cr " +
            "WHERE p1.user.userNum = :userNum1 AND p2.user.userNum = :userNum2 AND p1.isLeave = false AND p2.isLeave = false " +
            "GROUP BY cr.roomNum")
    Optional<ChatRoom> findChatRoomByParticipants(@Param("userNum1") Integer userNum1, @Param("userNum2") Integer userNum2);

}