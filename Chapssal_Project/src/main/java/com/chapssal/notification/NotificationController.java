package com.chapssal.notification;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chapssal.user.User;
import com.chapssal.user.UserService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;
    private final Sinks.Many<Notification> sink = Sinks.many().multicast().onBackpressureBuffer();

    @GetMapping("/unread")
    public List<Notification> getUnreadNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return List.of();
        }
        String userId = authentication.getName();
        User user = userService.findByUserId(userId);
        return notificationService.getUnreadNotificationsByUser(user);
    }
    
    @GetMapping("/all")
    public List<Notification> getAllNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return List.of();
        }
        String userId = authentication.getName();
        User user = userService.findByUserId(userId);
        return notificationService.getNotificationsByUser(user);
    }

    @PostMapping("/mark-as-read/{id}")
    public void markAsRead(@PathVariable("id") Long id) {
        notificationService.markAsRead(id);
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Notification> streamNotifications() {
        return sink.asFlux();
    }

    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        sink.tryEmitNext(event.getNotification());
    }
}
