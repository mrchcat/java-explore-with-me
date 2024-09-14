package com.github.mrchcat.explorewithme.user.service;

import com.github.mrchcat.explorewithme.user.dto.UserCreateDto;
import com.github.mrchcat.explorewithme.user.dto.UserDto;
import com.github.mrchcat.explorewithme.user.model.User;

import java.util.List;

public interface UserService {
    UserDto createUser(UserCreateDto createDto);

    void deleteUser(long id);

    List<UserDto> getSelectedUsers(List<Long> userIds);

    List<UserDto> getAllUsers(long from, long size);

    User getUserById(long userId);
}
