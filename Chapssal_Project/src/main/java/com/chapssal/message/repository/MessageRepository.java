package com.chapssal.message.repository;

import com.chapssal.message.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiver(int receiver); // 타입을 int로 수정
}