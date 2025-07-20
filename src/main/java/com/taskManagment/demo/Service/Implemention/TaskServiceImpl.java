package com.taskManagment.demo.Service.Implemention;

import com.taskManagment.demo.DTO.Task.TaskRequest;
import com.taskManagment.demo.DTO.Task.TaskResponse;
import com.taskManagment.demo.Entity.Task;
import com.taskManagment.demo.Entity.User;
import com.taskManagment.demo.Repo.TaskRepo;
import com.taskManagment.demo.Service.TaskService;
import com.taskManagment.demo.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepo taskRepo;
    private final UserService userService;


    @Override
    public TaskResponse createTask(TaskRequest request,String username){
        User user = userService.findByUsername(username);
        Task task = mapToTask(request);
        task.setUser(user);
        return mapToResponse(taskRepo.save(task));
    }

    @Override
    public Page<TaskResponse> getAllTask(String username,int page,int size) {
        User user = userService.findByUsername(username);
        Pageable pageable = PageRequest.of(page,size, Sort.by("deadline").ascending());
        Page<Task> tasks = taskRepo.findByUser(user,pageable);
        return tasks.map(this::mapToResponse);
    }


    @Override
    public TaskResponse updateTask(Long taskId,TaskRequest request,String username){
        Task task = taskRepo.findById(taskId)
                .orElseThrow(()->new RuntimeException("Task not found"));
        if(!task.getUser().getUsername().equals(username)){
            throw new RuntimeException("Not your task");
        }
        task.setTitle(request.getTitle());
        task.setDeadline(request.getDeadline());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDescription(request.getDescription());
        return mapToResponse(taskRepo.save(task));
    }

    @Override
    public void deleteTask(Long taskId, String username) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(()->new RuntimeException("Task not found"));
        if(!task.getUser().getUsername().equals(username)){
            throw new RuntimeException("Not your task");
        }
        taskRepo.delete(task);
    }

    @Override
    public List<TaskResponse> sortByDeadline(String username, int page, int size,String order) {
        List<Task> tasks = getAllTaskRaw(username);
        List<Task> sorted = mergeSortByDeadline(tasks);
        if("desc".equalsIgnoreCase(order)){
            Collections.reverse(sorted);
        }
        List<Task> paged = getPage(sorted,page,size);
        return paged.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> sortByPriority(String username, int page, int size,String order) {
        List<Task> tasks = getAllTaskRaw(username);
        List<Task> sorted = quickSortByPriority(tasks);
        if("desc".equalsIgnoreCase(order)){
            Collections.reverse(sorted);
        }
        List<Task> paged = getPage(sorted,page,size);
        return paged.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> searchTasks(String query, String username){
        List<Task> userTask = getAllTaskRaw(username);
        return userTask.stream()
                .filter(task -> (task.getTitle() != null && task.getTitle().toLowerCase().contains(query.toLowerCase())) ||
                        (task.getDescription() != null && task.getDescription().toLowerCase().contains(query.toLowerCase()))
                )
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    @Override
    public List<TaskResponse> filterByDateRange(LocalDateTime start, LocalDateTime end, String status, String username){
        List<Task> userTasks = getAllTaskRaw(username);
        return userTasks.stream()
                .filter(task -> {
                    boolean matches = true;
                    if(status!=null && !status.isEmpty()){
                        matches&=task.getStatus()!=null && task.getStatus().equalsIgnoreCase(status);
                    }
                    if(start!=null){
                        matches&=task.getDeadline()!=null && !task.getDeadline().isBefore(start);
                    }
                    if(end!=null){
                        matches&=task.getDeadline()!=null && !task.getDeadline().isAfter(end);
                    }
                    return matches;
                })
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }



    private List<Task> getAllTaskRaw(String username){
        User user = userService.findByUsername(username);
        return taskRepo.findByUser(user);
    }


    // Mapper
    private Task mapToTask(TaskRequest request) {
        return Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .deadline(request.getDeadline())
                .priority(request.getPriority())
                .status(request.getStatus())
                .build();
    }

    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDeadline(),
                task.getPriority(),
                task.getStatus()
        );
    }



    // Sorting
    private List<Task> mergeSortByDeadline(List<Task> tasks){
        if(tasks.size()<=1) return tasks;
        int mid = tasks.size()/2;
        List<Task> left = mergeSortByDeadline(tasks.subList(0,mid));
        List<Task> right = mergeSortByDeadline(tasks.subList(mid, tasks.size()));
        return mergeDeadline(left,right);
    }

    private List<Task> mergeDeadline(List<Task> left,List<Task> right){
        List<Task> sorted = new ArrayList<>();
        int i=0,j=0;
        while(i<left.size()&&j<right.size()){
            LocalDateTime leftDeadline = left.get(i).getDeadline();
            LocalDateTime rightDeadline = right.get(j).getDeadline();

            // Handle null deadlines - put them at the end
            if(leftDeadline == null && rightDeadline == null){
                sorted.add(left.get(i++));
            } else if(leftDeadline == null){
                sorted.add(right.get(j++));
            } else if(rightDeadline == null){
                sorted.add(left.get(i++));
            } else if(leftDeadline.isBefore(rightDeadline)){
                sorted.add(left.get(i++));
            } else {
                sorted.add(right.get(j++));
            }
        }
        sorted.addAll(left.subList(i, left.size()));
        sorted.addAll(right.subList(j, right.size()));
        return sorted;
    }

    private List<Task> quickSortByPriority(List<Task> tasks){
        if(tasks.size()<=1) return tasks;

        Task pivot = tasks.get(tasks.size()-1);
        Integer pivotPriority = pivot.getPriority();

        List<Task> left = new ArrayList<>();
        List<Task> right = new ArrayList<>();
        List<Task> pivots = new ArrayList<>();
        pivots.add(pivot);

        for(int i=0;i<tasks.size()-1;i++){
            Task current = tasks.get(i);
            Integer currPriority = current.getPriority();


            // Handle null priorities - put them at the end
            if(currPriority == null && pivotPriority == null){
                pivots.add(current);
            } else if(currPriority == null){
                right.add(current);
            } else if(pivotPriority == null){
                left.add(current);
            } else if(currPriority < pivotPriority){
                left.add(current);
            } else if (currPriority > pivotPriority) {
                right.add(current);
            } else {
                pivots.add(current);
            }
        }
        List<Task> sorted = new ArrayList<>();
        sorted.addAll(quickSortByPriority(left));
        sorted.addAll(pivots);
        sorted.addAll(quickSortByPriority(right));
        return sorted;
    }

    // Paging helper
    private List<Task> getPage(List<Task> list,int page,int size){
        int fromIndex = page*size;
        int toIndex = Math.min(fromIndex+size,list.size());
        if(fromIndex>=list.size()){
            return new ArrayList<>();
        }
        return list.subList(fromIndex,toIndex);
    }





    //    Admin
    @Override
    public List<TaskResponse> getAllTasksForAdmin(){
        List<Task> tasks = taskRepo.findAll();
        List<TaskResponse> responses = tasks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return responses;
    }

    @Override
    public void deleteAnyTask(Long taskId){
        Task task = taskRepo.findById(taskId)
                .orElseThrow(()->new RuntimeException("Task not found"));
        taskRepo.delete(task);
    }

    @Override
    public TaskResponse updateAnyTask(Long taskId, TaskRequest request){
        Task task = taskRepo.findById(taskId)
                .orElseThrow(()-> new RuntimeException("Task not found"));
        task.setTitle(request.getTitle());
        task.setStatus(request.getStatus());
        task.setDescription(request.getDescription());
        task.setDeadline(request.getDeadline());
        task.setPriority(request.getPriority());
        return mapToResponse(taskRepo.save(task));
    }

    @Override
    public int countTasks(String username) {
        User user = userService.findByUsername(username);
        return taskRepo.findByUser(user).size();
    }
}