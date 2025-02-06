package com.backend.multiUserApproval.repository;

import com.backend.multiUserApproval.model.db.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    List<Approval> findByTaskId(Long taskId);
    boolean existsByTaskIdAndApproverId(Long taskId, Long approverId);
    long countByTaskId(Long taskId);
}
