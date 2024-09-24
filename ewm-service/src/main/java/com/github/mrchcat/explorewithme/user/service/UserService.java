package com.github.mrchcat.explorewithme.user.service;

import com.github.mrchcat.explorewithme.user.dto.UserCreateDto;
import com.github.mrchcat.explorewithme.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserCreateDto createDto);

    void delete(long id);

    List<UserDto> getAllDto(List<Long> userIds, long from, long size);
}
