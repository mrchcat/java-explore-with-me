package com.github.mrchcat.explorewithme.event.dto;

import com.github.mrchcat.explorewithme.category.dto.CategoryDto;
import com.github.mrchcat.explorewithme.user.dto.UserShortDto;

import java.time.LocalDateTime;

public class EventShortDto {
    private long id;
    private String title;
    private String description;
    private String annotation;
    private CategoryDto category;
    private LocalDateTime eventDate;
    private boolean paid;
    private int confirmedRequests;
    private UserShortDto initiator;
    private long views;
}

