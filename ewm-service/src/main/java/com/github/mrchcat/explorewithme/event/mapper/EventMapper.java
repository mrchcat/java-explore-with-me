package com.github.mrchcat.explorewithme.event.mapper;

import com.github.mrchcat.explorewithme.category.dto.CategoryDto;
import com.github.mrchcat.explorewithme.category.mapper.CategoryMapper;
import com.github.mrchcat.explorewithme.event.dto.EventCreateDto;
import com.github.mrchcat.explorewithme.event.dto.EventDto;
import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.event.model.EventState;
import com.github.mrchcat.explorewithme.user.dto.UserShortDto;
import com.github.mrchcat.explorewithme.user.mapper.UserMapper;
import com.github.mrchcat.explorewithme.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EventMapper {

    public Event toEntity(User initiator, EventCreateDto ecd) {
        return Event.builder()
                .title(ecd.getTitle())
                .annotation(ecd.getAnnotation())
                .description(ecd.getDescription())
                .category(ecd.getCategory())
                .eventDate(ecd.getEventDate())
                .location(ecd.getLocation())
                .paid(ecd.isPaid())
                .participantLimit(ecd.getParticipantLimit())
                .requestModeration(ecd.isRequestModeration())
                .initiator(initiator)
                .state(EventState.PENDING)
                .build();
    }

    public EventDto toDto(Event e) {
        CategoryDto categoryDto = CategoryMapper.toDTO(e.getCategory());
        UserShortDto userShortDto = UserMapper.toShortDto(e.getInitiator());
        long views = 0;
        int confirmedRequests = 0;
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
                .confirmedRequests(confirmedRequests)
                .createdOn(e.getCreatedOn())
                .initiator(userShortDto)
                .publishedOn(e.getPublishedOn())
                .state(e.getState())
                .views(views)
                .build();
    }

    public List<EventDto> toDto(List<Event> eventList) {
        Map<Long, Tuple> eventMap = mapViewsAndRequests(eventList);
        return eventList.stream()
                .map(this::toDto)
                .peek(dto -> {
                    long eventId = dto.getId();
                    Tuple tuple = eventMap.get(eventId);
                    if (tuple != null) {
                        dto.setConfirmedRequests(tuple.confirmedRequests);
                        dto.setViews(tuple.confirmedRequests);
                    }
                }).toList();
    }

    private Map<Long, Tuple> mapViewsAndRequests(List<Event> eventList) {
        List<Long> eventIds = eventList.stream().map(Event::getId).toList();
        Map<Long, Tuple> map = new HashMap<>();
//        TODO ВОзврат из базы
        return map;
    }

    private record Tuple(Long views, Integer confirmedRequests) {
    }

}
