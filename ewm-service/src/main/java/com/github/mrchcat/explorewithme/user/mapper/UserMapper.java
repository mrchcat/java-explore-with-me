package com.github.mrchcat.explorewithme.user.mapper;

import com.github.mrchcat.explorewithme.user.dto.UserCreateDto;
import com.github.mrchcat.explorewithme.user.dto.UserDto;
import com.github.mrchcat.explorewithme.user.dto.UserShortDto;
import com.github.mrchcat.explorewithme.user.model.User;

import java.util.List;

public class UserMapper {

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static List<UserDto> toDto(List<User> users) {
        return users.stream().map(UserMapper::toDto).toList();
    }

    public static UserShortDto toShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static User toEntity(UserCreateDto createDto) {
        return User.builder()
                .email(createDto.getEmail())
                .name(createDto.getName())
                .build();
    }
}
