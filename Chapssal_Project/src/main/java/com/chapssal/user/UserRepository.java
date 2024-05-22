package com.chapssal.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByUserId(String userId);
	
    // 새로운 메소드 추가
    @Query("SELECT u FROM User u WHERE u.userId = :userId")
    Optional<User> findByUserId2(@Param("userId") String userId);
	
	@Query("SELECT u.userName FROM User u WHERE u.userId = :userId")
    Optional<String> findUserNameByUserId(@Param("userId") String userId);
	
    @Query("SELECT u.userNum FROM User u WHERE u.userId = :userId")
    Integer findUserNumByUserId(@Param("userId") String userId);
}
