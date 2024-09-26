package com.github.mrchcat.explorewithme.event.mapper;

import com.github.mrchcat.explorewithme.category.dto.CategoryDto;
import com.github.mrchcat.explorewithme.category.mapper.CategoryMapper;
import com.github.mrchcat.explorewithme.event.dto.EventDto;
import com.github.mrchcat.explorewithme.event.dto.EventShortDto;
import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.user.dto.UserShortDto;
import com.github.mrchcat.explorewithme.user.mapper.UserMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventMapper {

    private static EventDto toDtoExclViewsRequests(Event e) {
        CategoryDto categoryDto = CategoryMapper.toDTO(e.getCategory());
        UserShortDto userShortDto = UserMapper.toShortDto(e.getInitiator());
        return EventDto.builder()
                .id(e.getId())
                .title(e.getTitle())
                .annotation(e.getAnnotation())
                .description(e.getDescription())
                .category(categoryDto)
                .eventDate(e.getEventDate())
                .location(e.getLocation())
                .paid(e.isPaid())
                .participantLimit(e.getParticipantLimit())
                .requestModeration(e.isRequestModeration())
                .createdOn(e.getCreatedOn())
                .initiator(userShortDto)
                .publishedOn(e.getPublishedOn())
                .state(e.getState())
                .build();
    }

    public static EventDto toDto(Event e, long views, int participants) {
        EventDto eventDto = toDtoExclViewsRequests(e);
        eventDto.setViews(views);
        eventDto.setConfirmedRequests(participants);
        return eventDto;
    }

    public static List<EventDto> toDto(List<Event> events,
                                       Map<Long, Long> idViewMap,
                                       Map<Long, Integer> idParticipantMap) {
        return events.stream()
                .map(EventMapper::toDtoExclViewsRequests)
                .peek(dto -> dto.setViews(idViewMap.get(dto.getId())))
                .peek(dto -> dto.setConfirmedRequests(idParticipantMap.get(dto.getId())))
                .collect(Collectors.toList());
    }

    private static EventShortDto toShortDtoExclViews(Event e) {
        CategoryDto categoryDto = CategoryMapper.toDTO(e.getCategory());
        UserShortDto userShortDto = UserMapper.toShortDto(e.getInitiator());
        return EventShortDto.builder()
                .id(e.getId())
                .title(e.getTitle())
                .annotation(e.getAnnotation())
                .category(categoryDto)
                .eventDate(e.getEventDate())
                .paid(e.isPaid())
                .initiator(userShortDto)
                .build();
    }

    public static List<EventShortDto> toShortDto(List<Event> events,
                                                 Map<Long, Long> idViewMap,
                                                 Map<Long, Integer> idParticipantMap) {
        return events.stream()
                .map(EventMapper::toShortDtoExclViews)
                .peek(dto -> dto.setViews(idViewMap.get(dto.getId())))
                .peek(dto -> dto.setConfirmedRequests(idParticipantMap.get(dto.getId())))
                .collect(Collectors.toList());
    }
}
