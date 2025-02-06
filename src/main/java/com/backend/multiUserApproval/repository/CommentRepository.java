package com.backend.multiUserApproval.repository;

import com.backend.multiUserApproval.model.db.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}