package com.chapssal.topic;

import com.chapssal.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SelectedTopicService {

    private final SelectedTopicRepository selectedTopicRepository;

    @Autowired
    public SelectedTopicService(SelectedTopicRepository selectedTopicRepository) {
        this.selectedTopicRepository = selectedTopicRepository;
    }

    public List<SelectedTopic> findByUser(User user) {
        return selectedTopicRepository.findByUser(user);
    }
    
    public List<Object[]> findTopicsByVoteCount() {
        return selectedTopicRepository.findTopicsByVoteCount();
    }
}
