package com.taskManagment.demo.Service;

public interface NotificationService {
    void checkUpcomingTasks();
    void sendTaskCreatedNotification(String username, String taskTitle);
    void sendTaskCompletedNotification(String username, String taskTitle);
    void sendTaskOverdueNotification(String username, String taskTitle);
}
