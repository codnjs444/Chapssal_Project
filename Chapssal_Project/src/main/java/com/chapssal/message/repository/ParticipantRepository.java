package com.chapssal.message.repository;

import com.chapssal.message.model.ChatRoom;
import com.chapssal.message.model.Participant;
import com.chapssal.user.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {
    List<Participant> findByRoom_RoomNum(int roomNum);

    List<Participant> findByRoom(ChatRoom room);

    @Transactional
    void deleteByRoomAndUser(ChatRoom room, User user);

    List<Participant> findByRoomAndIsLeave(ChatRoom room, Boolean isLeave);

    Optional<Participant> findByRoomAndUser(ChatRoom room, User user);

    List<Participant> findByUser_UserNumAndIsLeave(int userNum, Boolean isLeave);

    @Query("SELECT p FROM Participant p WHERE p.room.roomNum = :roomNum AND p.isLeave = false")
    List<Participant> findActiveParticipantsByRoomNum(@Param("roomNum") int roomNum);

    @Modifying
    @Transactional
    @Query("UPDATE Participant p SET p.isLeave = false WHERE p.room = :room AND p.user = :user")
    void updateIsLeaveToFalse(@Param("room") ChatRoom room, @Param("user") User user);
}