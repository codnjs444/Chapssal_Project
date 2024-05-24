package com.chapssal.message.service;

import com.chapssal.message.model.Participant;
import com.chapssal.message.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipantService {
    @Autowired
    private ParticipantRepository participantRepository;

    public List<Participant> getParticipantsByRoomNum(int roomNum) {
        return participantRepository.findByRoom_RoomNum(roomNum);
    }

}
