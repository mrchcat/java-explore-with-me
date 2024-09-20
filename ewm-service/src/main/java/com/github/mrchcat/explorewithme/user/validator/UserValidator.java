package com.github.mrchcat.explorewithme.user.validator;

import com.github.mrchcat.explorewithme.exception.DataIntegrityException;
import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
import com.github.mrchcat.explorewithme.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void isUserEmailUnique(String userEmail) {
        if (userRepository.existsByEmail(userEmail)) {
            String message = String.format("Email=[%s] is not unique for user", userEmail);
            throw new DataIntegrityException(message);
        }
    }

    public void isUserIdExists(long userId) {
        if (!userRepository.existsById(userId)) {
            String message = String.format("User with id=%d was not found", userId);
            throw new ObjectNotFoundException(message);
        }
    }
}
