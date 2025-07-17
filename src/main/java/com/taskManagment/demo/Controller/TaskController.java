package com.taskManagment.demo.Controller;

import com.taskManagment.demo.DTO.Page.PageResponse;
import com.taskManagment.demo.DTO.Task.TaskRequest;
import com.taskManagment.demo.DTO.Task.TaskResponse;
import com.taskManagment.demo.Service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/task")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/create")
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request, Authentication authentication){
        String username = authentication.getName();
        return ResponseEntity.ok(taskService.createTask(request, username));
    }

    @GetMapping("/gettasks")
    public ResponseEntity<Page<TaskResponse>> getAllTask(Authentication authentication,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size){
        String username = authentication.getName();
        return ResponseEntity.ok(taskService.getAllTask(username,page,size));
    }



    @PutMapping("/update/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long taskId,@RequestBody TaskRequest request,Authentication authentication){
        String username = authentication.getName();
        return ResponseEntity.ok(taskService.updateTask(taskId,request,username));
    }

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<String> delete(@PathVariable Long taskId, Authentication  authentication){
        String username = authentication.getName();
        taskService.deleteTask(taskId,username);
        return ResponseEntity.ok("Successfully deleted");
    }

    @GetMapping("/deadline")
    public ResponseEntity<PageResponse<TaskResponse>> sortByDeadLine(Authentication authentication,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(defaultValue = "asc") String order){
        String username = authentication.getName();
        List<TaskResponse> pageContent = taskService.sortByDeadline(username,page,size,order);
        int totalTasks = taskService.countTasks(username);
        int totalPages = (int) Math.ceil((double) totalTasks/size);
        return ResponseEntity.ok(new PageResponse<>(pageContent,totalPages,page,totalTasks));
    }

    @GetMapping("/priority")
    public ResponseEntity<PageResponse<TaskResponse>> sortByPriority(Authentication authentication,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size,
                                                                     @RequestParam(defaultValue = "asc") String order){
        String username = authentication.getName();
        List<TaskResponse> pageContent = taskService.sortByPriority(username,page,size,order);
        int totalTasks = taskService.countTasks(username);
        int totalPages = (int) Math.ceil((double) totalTasks/size);
        return ResponseEntity.ok(new PageResponse<>(pageContent,totalPages,page,totalTasks));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskResponse>> searchTasks(@RequestParam String query, Authentication authentication){
        String username = authentication.getName();
        return ResponseEntity.ok(taskService.searchTasks(query,username));
    }

//    @GetMapping("/status")
//    public ResponseEntity<List<TaskResponse>> filterByStatus(@RequestParam String value, Authentication authentication){
//        String username = authentication.getName();
//        return ResponseEntity.ok(taskService.filterByStatus(value,username));
//    }

    @GetMapping("/filter")
    public ResponseEntity<List<TaskResponse>> filterTask(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) String status,
            Authentication authentication
            ){
        String username = authentication.getName();
        return ResponseEntity.ok(taskService.filterByDateRange(start,end,status,username));
    }

}
