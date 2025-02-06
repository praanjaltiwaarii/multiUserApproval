package com.backend.multiUserApproval.controller;

import com.backend.multiUserApproval.model.db.Task;
import com.backend.multiUserApproval.model.dto.*;
import com.backend.multiUserApproval.service.TaskService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/task")
@Slf4j
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/create")
    public ResponseEntity<Task> createTask(@Valid @RequestBody CreateTaskRequest request) {
        log.info("Creating task: {}", request.getTitle());
        Task task = taskService.createTask(
                request.getCreatorId(),
                request.getTitle(),
                request.getDescription(),
                request.getApproverIds(),
                request.getComments()
        );

        log.info("Task Created Successfully ID: {}", task.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTask(@PathVariable Long taskId) {
        log.info("Fetching task with ID: {}", taskId);
        Task task = taskService.getTask(taskId);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/updateStatus")
    public ResponseEntity<?> updateTaskStatus(
            @Valid @RequestBody UpdateTaskStatusRequest request) {
        log.info("Updating task status for task ID: {}", request.getTaskId());
        Task task = taskService.updateTaskStatus(request);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/addComments")
    public ResponseEntity<?> addTaskComment(
            @Valid @RequestBody AddTaskCommentRequest request) {
        log.info("Adding comment to task ID: {}", request.getTaskId());
        Task task = taskService.addTaskComment(request.getTaskId(), request.getCommentText(), request.getAuthorId());
        return ResponseEntity.ok("Added Comment to taskId : "+task.getId());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("Validation errors: {}", errors);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralExceptions(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + ex.getMessage());
    }
}