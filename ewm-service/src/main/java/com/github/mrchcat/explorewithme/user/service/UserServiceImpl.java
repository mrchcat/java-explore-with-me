package com.github.mrchcat.explorewithme.user.service;

import com.github.mrchcat.explorewithme.exception.DataIntegrityException;
import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
import com.github.mrchcat.explorewithme.user.dto.UserCreateDto;
import com.github.mrchcat.explorewithme.user.dto.UserDto;
import com.github.mrchcat.explorewithme.user.mapper.UserMapper;
import com.github.mrchcat.explorewithme.user.model.User;
import com.github.mrchcat.explorewithme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserCreateDto createDto) {
        isEmailUnique(createDto.getEmail());
        User savedUser = userRepository.save(UserMapper.toEntity(createDto));
        return UserMapper.toDto(savedUser);
    }

    @Override
    public void deleteUser(long userId) {
        isIdExists(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getSelectedUsers(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        return users.stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public List<UserDto> getAllUsers(long from, long size) {
        List<User> users = userRepository.getAllUsers(from, size);
        return users.stream()
                .map(UserMapper::toDto)
                .toList();
    }

    private void isEmailUnique(String email) {
        if (userRepository.existsByEmail(email)) {
            String message = String.format("Email=[%s] is not unique for user", email);
            throw new DataIntegrityException(message);
        }
    }

    private void isIdExists(long userId) {
        if (!userRepository.existsById(userId)) {
            String message = String.format("User with id=%d was not found", userId);
            throw new ObjectNotFoundException(message);
        }
    }
}
