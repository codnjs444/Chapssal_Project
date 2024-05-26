package com.chapssal.message.repository;

import com.chapssal.message.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {
    List<Participant> findByRoom_RoomNum(int roomNum);

}