package com.chapssal.message.controller;

import com.chapssal.message.model.Message;
import com.chapssal.message.service.MessageService;
import com.chapssal.user.User;
import com.chapssal.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final Map<Integer, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public Message saveMessage(@RequestBody Message message) {
        // 메시지 저장 로직
        Message savedMessage = messageService.addMessage(message);

        // 수신자에게 SSE 이벤트 전송
        sendSseEventToUser(message.getReceiver(), "새로운 메시지가 도착했습니다.");

        return savedMessage;
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Integer>> getUnreadMessageCount(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = extractUsername(principal);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        int count = messageService.countUnreadMessagesForUser(username);
        Map<String, Integer> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    private String extractUsername(Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) principal;
            return oauthToken.getName();
        } else if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return null;
        }
    }

    public void sendSseEventToUser(int userId, String messageContent) {
        SseEmitter emitter = sseEmitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("message").data(messageContent));
                System.out.println("SSE event sent to user: " + userId); // 디버깅 로그 추가
            } catch (IOException e) {
                emitter.completeWithError(e);
                sseEmitters.remove(userId);
                System.err.println("Error sending SSE event: " + e.getMessage()); // 디버깅 로그 추가
            }
        }
    }

    @GetMapping("/info")
    public ResponseEntity<User> getUserInfo(Principal principal) {
        if (principal == null) {
            System.out.println("Principal is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = extractUsername(principal);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findByUserId(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/subscribe")
    public ResponseEntity<SseEmitter> subscribe(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = extractUsername(principal);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findByUserId(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        int userId = user.getUserNum();

        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30 minutes
        sseEmitters.put(userId, emitter);

        emitter.onCompletion(() -> sseEmitters.remove(userId));
        emitter.onTimeout(() -> sseEmitters.remove(userId));
        emitter.onError((e) -> sseEmitters.remove(userId));

        try {
            emitter.send(SseEmitter.event().name("INIT"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return ResponseEntity.ok(emitter);
    }
}
