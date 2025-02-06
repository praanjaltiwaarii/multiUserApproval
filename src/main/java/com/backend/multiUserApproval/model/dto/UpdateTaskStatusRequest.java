package com.backend.multiUserApproval.model.dto;

import com.backend.multiUserApproval.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateTaskStatusRequest {
    @NotNull(message = "Task ID is required")
    private Long taskId;

    @NotNull(message = "Author ID is required")
    private Long authorId;

    @NotNull(message = "Task status is required")
    private TaskStatus status;

    private List<String> comments;
}