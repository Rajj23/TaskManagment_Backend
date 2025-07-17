package com.taskManagment.demo.Config;

import com.taskManagment.demo.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final NotificationService notificationService;

    // Check for upcoming tasks every 15 minutes
    @Scheduled(fixedRate = 900000) // 15 minutes in milliseconds
    public void checkUpcomingDeadlines() {
        notificationService.checkUpcomingTasks();
    }
}
