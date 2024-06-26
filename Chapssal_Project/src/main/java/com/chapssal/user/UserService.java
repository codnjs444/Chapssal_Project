package com.chapssal.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chapssal.DataNotFoundException;
import com.chapssal.school.School;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findById(Integer userId) {
        return userRepository.findById(userId);
    }
    
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
        user.setUserName("user_" + userId);
        user.setLastLogin(lastLogin);
        user.setTopic(0); // 명시적으로 유저 테이블의 topic 컬럼을 0으로 설정
        user.setVote(0); // 명시적으로 유저 테이블의 vote 컬럼을 0으로 설정


        userRepository.save(user);
        return user;
    }


    public User createSocialUser(String userId, String userName) {
        User user = new User();
        user.setUserId(userId);
        user.setUserName(userName);
        user.setCreateDate(LocalDateTime.now());
        user.setTopic(0); // 명시적으로 유저 테이블의 topic 컬럼을 0으로 설정
        user.setVote(0); // 명시적으로 유저 테이블의 vote 컬럼을 0으로 설정

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
                .orElseGet(() -> {
                    // currentUserNum을 사용자 정보에서 가져오는 로직
                    Optional<User> user = userRepository.findByUserId(userId);
                    return user.map(u -> "사용자" + u.getUserNum()).orElse("사용자");
                });
    }


    public User findByUserNum(Integer userNum) {
        return userRepository.findById(userNum).orElse(null);
    }

    // 유저 투표시 vote 필드 1 증가
    public void incrementVote(User user) {
        user.setVote(user.getVote() + 1);
        userRepository.save(user);
    }

    // 유저가 이번주 최대 투표수에 도달했는지 판별
    public boolean hasReachedVoteLimit(User user) {
        return user.getVote() >= 5;
    }

    // 유저 테이블의 topic 컬럼을 0으로 초기화하는 메서드
    @Transactional
    public void resetAllTopics() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.setTopic(0);
        }
        userRepository.saveAll(users);
    }

    // 유저 테이블의 vote 컬럼을 0으로 초기화하는 메서드
    @Transactional
    public void resetAllVotes() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.setVote(0);
        }
        userRepository.saveAll(users);
    }

    public Optional<User> findByUserId2(String userId) {
        return userRepository.findByUserId2(userId);
    }



    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getUserSuggestions(String query) {
        return userRepository.findByUserNameContaining(query); // 수정된 부분
    }
    
    // 전체 검색 관련
    public List<User> searchByUserName(String userName) {
        return userRepository.findByUserNameContaining(userName);
    }

    public List<String> findUserNamesByQuery(String query) {
        return userRepository.findByUserNameContaining(query)
                .stream()
                .map(User::getUserName)
                .collect(Collectors.toList());
    }
}
