package com.github.mrchcat.explorewithme.event.dto;

import com.github.mrchcat.explorewithme.event.model.EventState;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
public class EventSearchDto {
    List<Long> userIds;
    List<EventState> states;
    List<Long> categoryIds;
    LocalDateTime start;
    LocalDateTime end;
    Integer from;
    Integer size;
}
