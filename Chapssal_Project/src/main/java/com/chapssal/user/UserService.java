package com.chapssal.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chapssal.DataNotFoundException;
import com.chapssal.school.School;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User create(String userId, String password, School school, LocalDateTime createDate, LocalDateTime lastUpdate, LocalDateTime lastLogin) {
        Optional<User> existingUser = userRepository.findByUserId(userId);
        if (existingUser.isPresent()) {
            throw new DuplicateUserIdException("이미 존재하는 아이디입니다.");
        }

        User user = new User();
        user.setUserId(userId);
        user.setPassword(passwordEncoder.encode(password));
        user.setSchool(school);
        user.setCreateDate(createDate);
        user.setLastUpdate(lastUpdate);
        user.setLastLogin(lastLogin);

        userRepository.save(user);
        return user;
    }

    
    public User createSocialUser(String userId, String userName) {
        User user = new User();
        user.setUserId(userId);
        user.setUserName(userName);
        user.setCreateDate(LocalDateTime.now());

        userRepository.save(user);
        return user;
    }
    
    public User getUser(String userid) {
		Optional<User> siteUser = this.userRepository.findByUserId(userid);
		if(siteUser.isPresent()) {
			return siteUser.get();
		} else {
			throw new DataNotFoundException("siteuser not found");
		}
		
	}

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void save(User user) {
        userRepository.save(user);
    }
    
    public User updateUserName(String userId, String newUserName, String bio) {
        User user = userRepository.findByUserId(userId).orElse(new User());
        user.setUserName(newUserName);
        user.setBio(bio);
        return userRepository.save(user);
    }
    
    public String getUserBioByUserId(String userId) {
        return userRepository.findByUserId(userId)
                             .map(User::getBio)
                             .orElse("");
    }
    
    public String getSchoolNameByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .map(User::getSchool)
                .map(School::getSchoolName)
                .orElse("학교 정보 없음");
    }
    
    public Integer getUserNumByUserId(String userId) {
        return userRepository.findUserNumByUserId(userId);
    }
    
    public String getUserNameByUserId(String userId) {
        return userRepository.findUserNameByUserId(userId)
                             .orElse("사용자"); // 사용자 이름이 없을 경우 "사용자"를 반환
    }
    
    public User findByUserNum(Integer userNum) {
        return userRepository.findById(userNum).orElse(null);
    }

}
