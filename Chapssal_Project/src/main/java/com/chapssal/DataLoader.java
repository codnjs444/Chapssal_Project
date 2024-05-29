//package com.chapssal;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class DataLoader implements CommandLineRunner {
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // 임시 유저 데이터 생성
//        List<Object[]> userData = new ArrayList<>();
//        LocalDateTime now = LocalDateTime.now();
//
//        for (int i = 1; i <= 100; i++) {
//            String userId = "user" + i;
//            String userName = "사용자" + i;
//            String rawPassword = String.valueOf(i); // 원본 비밀번호를 문자열로 변환
//            String encryptedPassword = passwordEncoder.encode(rawPassword);
//            int school = 1;
//            String bio = "This is a bio for user " + i;
//            int topic = i % 10 + 1; // 임의의 토픽 번호
//            LocalDateTime createDate = now;
//            String phoneNum = null;
//            String job = null;
//            int authority = 0;
//            String profilePictureUrl = null;
//
//            userData.add(new Object[]{userId, userName, encryptedPassword, profilePictureUrl, school, bio, topic, createDate, null, null, phoneNum, job, authority});
//        }
//
//        // SQL 쿼리 실행
//        String sql = "INSERT INTO User (userId, userName, password, profilePictureUrl, school, bio, topic, createDate, lastUpdate, lastLogin, phone, job, authority) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        jdbcTemplate.batchUpdate(sql, userData);
//
//        System.out.println("100개의 임시 유저 데이터가 성공적으로 삽입되었습니다.");
//    }
//}
