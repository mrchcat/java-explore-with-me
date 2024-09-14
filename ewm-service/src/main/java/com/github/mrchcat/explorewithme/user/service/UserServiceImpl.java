package com.github.mrchcat.explorewithme.user.service;

import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
import com.github.mrchcat.explorewithme.user.dto.UserCreateDto;
import com.github.mrchcat.explorewithme.user.dto.UserDto;
import com.github.mrchcat.explorewithme.user.mapper.UserMapper;
import com.github.mrchcat.explorewithme.user.model.User;
import com.github.mrchcat.explorewithme.user.repository.UserRepository;
import com.github.mrchcat.explorewithme.validator.Validator;
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
    private final Validator validator;

    @Override
    public UserDto createUser(UserCreateDto createDto) {
        validator.isUserEmailUnique(createDto.getEmail());
        User savedUser = userRepository.save(UserMapper.toEntity(createDto));
        log.info("{} added}", savedUser);
        return UserMapper.toDto(savedUser);
    }

    @Override
    public void deleteUser(long userId) {
        validator.isUserIdExists(userId);
        userRepository.deleteById(userId);
        log.info("User with id={} deleted", userId);
    }

//    @Override
//    public List<UserDto> getSelectedUsers(List<Long> userIds) {
//        List<User> users = userRepository.findAllById(userIds);
//        return users.stream()
//                .map(UserMapper::toDto)
//                .toList();
//    }

//    @Override
//    public List<UserDto> getAllUsers(long from, long size) {
//        List<User> users = userRepository.getAllUsers(from, size);
//        return users.stream()
//                .map(UserMapper::toDto)
//                .toList();
//    }


    @Override
    public List<UserDto> getAllUsers(List<Long> userIds, long from, long size) {
        List<User> users;
        if(userIds==null||userIds.isEmpty()){
            users=userRepository.getAllUsers(from, size);
        } else{
            users = userRepository.getSelectedUsers(userIds, from, size);
        }
        return users.stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public User getUserById(long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.orElseThrow(() -> {
            String message = String.format("User with id=%d was not found", userId);
            return new ObjectNotFoundException(message);
        });
    }


}
