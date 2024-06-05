package com.chapssal.message.service;

import com.chapssal.message.model.Participant;
import com.chapssal.user.User;
import com.chapssal.message.repository.ParticipantRepository;
import com.chapssal.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipantService {
    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Participant> getParticipantsByRoomNum(int roomNum) {
        return participantRepository.findByRoom_RoomNum(roomNum);
    }

    public List<User> searchUsers(String query, boolean isEnglish) {
        if (isEnglish) {
            return userRepository.searchByUserId(query);
        } else {
            return userRepository.searchByUserName(query);
        }
    }

}
