package com.backend.multiUserApproval.service;

import com.backend.multiUserApproval.exceptions.UserAlreadyExistsException;
import com.backend.multiUserApproval.exceptions.UserNotFoundException;
import com.backend.multiUserApproval.model.db.User;
import com.backend.multiUserApproval.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String name, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException(email);
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        return userRepository.save(user);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

}
