package com.github.mrchcat.explorewithme.event.dto;

import com.github.mrchcat.explorewithme.category.dto.CategoryDto;
import com.github.mrchcat.explorewithme.category.model.Category;
import com.github.mrchcat.explorewithme.event.model.EventState;
import com.github.mrchcat.explorewithme.event.model.Location;
import com.github.mrchcat.explorewithme.user.dto.UserShortDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class EventDto {
    private long id;
    private String title;
    private String annotation;
    private String description;
    private CategoryDto category;
    private LocalDateTime eventDate;
    private Location location;
    private boolean paid;
    private long participantLimit;
    private boolean requestModeration;
    private int confirmedRequests;
    private LocalDateTime createdOn;
    private UserShortDto initiator;
    private LocalDateTime publishedOn;
    private EventState state;
    private long views;
}
