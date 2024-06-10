package com.chapssal.hashtag;

import com.chapssal.user.User;
import com.chapssal.video.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsertagService {

    private final UsertagRepository usertagRepository;

    @Autowired
    public UsertagService(UsertagRepository usertagRepository) {
        this.usertagRepository = usertagRepository;
    }

    @Transactional
    public void saveUsertag(Video video, User user) {
        if (!usertagRepository.existsByVideoAndUser(video, user)) {
            Usertag usertag = new Usertag();
            usertag.setVideo(video);
            usertag.setUser(user);
            usertagRepository.save(usertag);
        }
    }
}
