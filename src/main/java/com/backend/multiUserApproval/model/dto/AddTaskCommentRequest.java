package com.backend.multiUserApproval.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddTaskCommentRequest {
    @NotNull(message = "Task ID is required")
    private Long taskId;

    @NotBlank(message = "Comment text is required")
    @Size(min = 5, message = "Comment must be at least 5 characters")
    private String commentText;

    @NotNull(message = "Author ID is required")
    private Long authorId;
}