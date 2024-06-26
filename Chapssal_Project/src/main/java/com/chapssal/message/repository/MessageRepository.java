package com.chapssal.message.repository;

import com.chapssal.message.model.ChatRoom;
import com.chapssal.message.model.Message;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoom_RoomNum(int roomNum);

    Optional<Message> findTopByChatRoomOrderBySendDateDesc(ChatRoom chatRoom);

    // 특정 채팅방에서 메시지 ID보다 작은 메시지들을 가져오는 쿼리
    @Query("SELECT m FROM Message m WHERE m.chatRoom.roomNum = :roomNum AND m.messageNum < :oldestMessageId ORDER BY m.sendDate DESC")
    List<Message> findByChatRoom_RoomNumAndMessageNumLessThanOrderBySendDateDesc(
            @Param("roomNum") int roomNum,
            @Param("oldestMessageId") int oldestMessageId,
            Pageable pageable
    );

    // 특정 채팅방의 최신 메시지들을 가져오는 쿼리
    @Query("SELECT m FROM Message m WHERE m.chatRoom.roomNum = :roomNum ORDER BY m.sendDate DESC")
    List<Message> findByChatRoom_RoomNumOrderBySendDateDesc(
            @Param("roomNum") int roomNum,
            Pageable pageable
    );



    @Query("SELECT COUNT(m) FROM Message m WHERE m.chatRoom.roomNum = :roomNum AND m.receiver = :userNum AND m.isRead = 0")
    long countByChatRoom_RoomNumAndReceiver_UserNumAndIsRead(
            @Param("roomNum") int roomNum,
            @Param("userNum") int userNum
    );

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = 1 WHERE m.chatRoom.roomNum = :roomNum AND m.receiver = :userNum AND m.isRead = 0")
    void updateIsReadByChatRoom_RoomNumAndReceiver_UserNum(
            @Param("roomNum") int roomNum,
            @Param("userNum") int userNum
    );

    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver = :userNum AND m.isRead = 0")
    int countUnreadMessages2(@Param("userNum") int userNum);

}