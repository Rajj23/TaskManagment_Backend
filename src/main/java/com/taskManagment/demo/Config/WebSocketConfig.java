package com.taskManagment.demo.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        // Enable simple broker for topics and queues
        config.enableSimpleBroker("/topic", "/queue");

        // Set application destination prefix for client messages
        config.setApplicationDestinationPrefixes("/app");

        // Set user destination prefix for user-specific messages
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        // Register WebSocket endpoint with proper CORS configuration
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:5173", "http://localhost:3000","https://organizotask.vercel.app/") // More secure than *
                .withSockJS(); // Enable SockJS fallback

        // Also register without SockJS for native WebSocket clients
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:5173", "http://localhost:3000","https://organizotask.vercel.app/");
    }
}
