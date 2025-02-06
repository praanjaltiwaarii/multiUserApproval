package com.backend.multiUserApproval.service;

import com.backend.multiUserApproval.enums.TaskStatus;
import com.backend.multiUserApproval.exceptions.TaskNotFoundException;
import com.backend.multiUserApproval.exceptions.UserNotFoundException;
import com.backend.multiUserApproval.model.db.Approval;
import com.backend.multiUserApproval.model.db.Comment;
import com.backend.multiUserApproval.model.db.Task;
import com.backend.multiUserApproval.model.db.User;
import com.backend.multiUserApproval.model.dto.UpdateTaskStatusRequest;
import com.backend.multiUserApproval.repository.ApprovalRepository;
import com.backend.multiUserApproval.repository.TaskRepository;
import com.backend.multiUserApproval.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ApprovalRepository approvalRepository;
    private final EmailService emailService;

    @Value("${task.approval.minimum-three-enabled:false}")
    private boolean minimumThreeApprovalEnabled;

    public TaskService(TaskRepository taskRepository,
                       UserRepository userRepository,
                       ApprovalRepository approvalRepository,
                       EmailService emailService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.approvalRepository = approvalRepository;
        this.emailService = emailService;
    }

    public Task createTask(Long creatorId, String title, String description,
                           List<Long> approverIds, List<String> comments) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new UserNotFoundException(creatorId));

        Set<User> approvers = new HashSet<>(userRepository.findAllById(approverIds));
        if (approvers.isEmpty()) {
            throw new IllegalArgumentException("At least one approver is required");
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(TaskStatus.PENDING);
        task.setCreator(creator);
        task.setApprovers(approvers);

        if (comments != null && !comments.isEmpty()) {
            for (String commentText : comments) {
                Comment comment = new Comment();
                comment.setText(commentText);
                comment.setAuthor(creator);
                comment.setTask(task);
                task.getComments().add(comment);
            }
        }

        Task savedTask = taskRepository.save(task);
        emailService.notifyNewTask(savedTask);

        return savedTask;
    }

    public Task updateTaskStatus(UpdateTaskStatusRequest request) {
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new TaskNotFoundException(request.getTaskId()));

        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new UserNotFoundException(request.getAuthorId()));

        if (!task.getApprovers().contains(author)) {
            throw new IllegalStateException("Only approvers can update task status");
        }

        if (request.getComments() != null && !request.getComments().isEmpty()) {
            for (String commentText : request.getComments()) {
                Comment comment = new Comment();
                comment.setText(commentText);
                comment.setAuthor(author);
                comment.setTask(task);
                task.getComments().add(comment);
            }
        }

        if (request.getStatus() == TaskStatus.APPROVED && !hasApproved(task.getId(), author.getId())) {

            Approval approval = new Approval();
            approval.setTask(task);
            approval.setApprover(author);
            approval.setApprovedAt(LocalDateTime.now());
            approvalRepository.save(approval);

            log.info("Marked Approval for Task: {} from Approver: {}", task.getId(), author.getId());

            emailService.notifyApproval(task, author);

            if (isFullyApproved(task)) {
                task.setStatus(TaskStatus.APPROVED);
                log.info("Task: {} moved to status APPROVED", task.getId());
                emailService.notifyFullApproval(task);
            }
        }

        return taskRepository.save(task);
    }

    public Task addTaskComment(Long taskId, String commentText, Long authorId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException(authorId));

        Comment comment = new Comment();
        comment.setText(commentText);
        comment.setAuthor(author);
        comment.setTask(task);
        task.getComments().add(comment);

        return taskRepository.save(task);
    }

    private boolean hasApproved(Long taskId, Long approverId) {
        return approvalRepository.existsByTaskIdAndApproverId(taskId, approverId);
    }

    private boolean isFullyApproved(Task task) {
        long approvalCount = approvalRepository.countByTaskId(task.getId());
        int totalApprovers = task.getApprovers().size();

        if (minimumThreeApprovalEnabled && totalApprovers > 3) {
            return approvalCount >= 3;
        }

        return approvalCount == totalApprovers;
    }

    public Task getTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

}