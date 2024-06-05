package com.chapssal.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByUserId(String userId);

    @Query("SELECT u FROM User u WHERE u.userId = :userId")
    Optional<User> findByUserId2(@Param("userId") String userId);

    @Query("SELECT u FROM User u WHERE u.userNum = :userNum")
    Optional<User> findByUserNum(@Param("userNum") int userNum);

	@Query("SELECT u.userName FROM User u WHERE u.userId = :userId")
    Optional<String> findUserNameByUserId(@Param("userId") String userId);
	
    @Query("SELECT u.userNum FROM User u WHERE u.userId = :userId")
    Integer findUserNumByUserId(@Param("userId") String userId);

    @Query("SELECT p.user FROM Participant p WHERE p.room.roomNum = :roomNum AND p.user.userNum != :currentUserNum")
    List<User> findOtherParticipants(@Param("roomNum") Integer roomNum, @Param("currentUserNum") Integer currentUserNum);


    //메신저에서 유저 찾기
    @Query("SELECT u FROM User u WHERE LOWER(u.userId) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<User> searchByUserId(@Param("query") String query);

    @Query("SELECT u FROM User u WHERE u.userName LIKE CONCAT('%', :query, '%')")
    List<User> searchByUserName(@Param("query") String query);

    //전체 찾기
    List<User> findByUserNameContaining(String userName);
}
