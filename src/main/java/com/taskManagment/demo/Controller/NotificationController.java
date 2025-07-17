package com.taskManagment.demo.Controller;

import com.taskManagment.demo.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/check")
    public ResponseEntity<String> checkUpcomingTasks() {
        notificationService.checkUpcomingTasks();
        return ResponseEntity.ok("Notification check completed");
    }
}
