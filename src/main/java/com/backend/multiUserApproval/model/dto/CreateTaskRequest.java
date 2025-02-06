package com.backend.multiUserApproval.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateTaskRequest {
    @NotNull(message = "Creator ID is required")
    private Long creatorId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "At least one approver is required")
    @Size(min = 1, message = "At least one approver is required")
    private List<Long> approverIds;

    private List<String> comments;
}