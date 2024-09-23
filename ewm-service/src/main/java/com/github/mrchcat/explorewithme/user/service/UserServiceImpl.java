package com.github.mrchcat.explorewithme.user.service;

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
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserCreateDto createDto) {
        User savedUser = userRepository.save(UserMapper.toEntity(createDto));
        log.info("{} added}", savedUser);
        return UserMapper.toDto(savedUser);
    }

    @Override
    public void delete(long userId) {
        userRepository.deleteById(userId);
        log.info("User with id={} deleted or not exists", userId);
    }

    @Override
    public List<UserDto> getAllDto(List<Long> userIds, long from, long size) {
        List<User> users;
        if (userIds == null || userIds.isEmpty()) {
            users = userRepository.getAllUsers(from, size);
        } else {
            users = userRepository.getSelectedUsers(userIds, from, size);
        }
        return UserMapper.toDto(users);
    }

    @Override
    public User getById(long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.orElseThrow(() -> {
            String message = String.format("User with id=%d was not found", userId);
            return new ObjectNotFoundException(message);
        });
    }
}
