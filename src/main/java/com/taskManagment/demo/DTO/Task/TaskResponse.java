package com.taskManagment.demo.DTO.Task;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private int priority;
    private String status;
}
