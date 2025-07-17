package com.taskManagment.demo.DTO.Task;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private LocalDateTime deadline;
    private int priority;
    private String status;
}
