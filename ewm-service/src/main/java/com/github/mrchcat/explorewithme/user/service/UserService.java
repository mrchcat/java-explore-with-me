package com.github.mrchcat.explorewithme.user.service;

import com.github.mrchcat.explorewithme.user.dto.UserCreateDto;
import com.github.mrchcat.explorewithme.user.dto.UserDto;
import com.github.mrchcat.explorewithme.user.model.User;

import java.util.List;

public interface UserService {
    UserDto create(UserCreateDto createDto);

    void delete(long id);

//    List<UserDto> getSelectedUsers(List<Long> userIds);
//
//    List<UserDto> getAllUsers(long from, long size);

    User getById(long userId);

    List<UserDto> getAllDto(List<Long> userIds, long from, long size);
}
