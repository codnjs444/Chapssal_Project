package com.chapssal.message.repository;

import com.chapssal.message.model.ChatRoom;
import com.chapssal.message.model.Participant;
import com.chapssal.user.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {
    List<Participant> findByRoom_RoomNum(int roomNum);

    @Transactional
    void deleteByRoomAndUser(ChatRoom room, User user);

}