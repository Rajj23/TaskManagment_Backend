package com.taskManagment.demo.Controller;

import com.taskManagment.demo.DTO.Notification.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/test")
    public void handleTestMessage(@Payload NotificationMessage message, Principal principal) {
        log.info("Received test message from user: {}", principal != null ? principal.getName() : "anonymous");

        // Echo the message back to the sender
        if (principal != null) {
            messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/notifications",
                new NotificationMessage(principal.getName(), "Test message received: " + message.getMessage())
            );
        }
    }

    @MessageMapping("/subscribe")
    public void handleSubscription(Principal principal) {
        if (principal != null) {
            log.info("User {} subscribed to notifications", principal.getName());
            messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/notifications",
                new NotificationMessage(principal.getName(), "Successfully connected to notifications")
            );
        }
    }
}
