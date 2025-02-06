package com.backend.multiUserApproval.service;

import com.backend.multiUserApproval.model.db.Task;
import com.backend.multiUserApproval.model.db.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }

    public void notifyNewTask(Task task) {
        String subject = "New Task for Approval";
        for (User approver : task.getApprovers()) {
            String body = String.format("New task '%s' requires your approval.\nCreated by: %s",
                    task.getTitle(), task.getCreator().getName());
            sendEmail(approver.getEmail(), subject, body);
        }
    }

    public void notifyApproval(Task task, User approver) {
        String subject = "Task Approval Update";
        String body = String.format("User %s has approved task '%s'",
                approver.getName(), task.getTitle());
        sendEmail(task.getCreator().getEmail(), subject, body);
    }

    public void notifyFullApproval(Task task) {
        String subject = "Task Fully Approved";
        String body = String.format("Task '%s' has been fully approved by all approvers",
                task.getTitle());

        sendEmail(task.getCreator().getEmail(), subject, body);

        for (User approver : task.getApprovers()) {
            sendEmail(approver.getEmail(), subject, body);
        }
    }
}