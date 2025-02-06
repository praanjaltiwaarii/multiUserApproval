package com.backend.multiUserApproval.controller;

import com.backend.multiUserApproval.exceptions.UserAlreadyExistsException;
import com.backend.multiUserApproval.model.db.User;
import com.backend.multiUserApproval.model.dto.UserSignInRequest;
import com.backend.multiUserApproval.security.JwtService;
import com.backend.multiUserApproval.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    private JwtService jwtService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserSignInRequest request) {
        try {
            User user = userService.registerUser(request.getName(), request.getEmail(), request.getPassword());
            String token = jwtService.generateToken(user.getEmail());
            user.setToken(token);
            log.info("User {} Created Successfully", user.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserSignInRequest request) {
        User foundUser = userService.findUserByEmail(request.getEmail());
        if (foundUser != null && foundUser.getPassword().equals(request.getPassword())) {
            String token = jwtService.generateToken(foundUser.getEmail());
            log.info("User {} LoggedIn Successfully", foundUser.getName());
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> response = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                response.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(response);
    }


}
