package com.github.mrchcat.explorewithme.event.mapper;

import com.github.mrchcat.explorewithme.StatHttpClient;
import com.github.mrchcat.explorewithme.category.dto.CategoryDto;
import com.github.mrchcat.explorewithme.category.mapper.CategoryMapper;
import com.github.mrchcat.explorewithme.category.model.Category;
import com.github.mrchcat.explorewithme.category.service.CategoryService;
import com.github.mrchcat.explorewithme.event.dto.EventCreateDto;
import com.github.mrchcat.explorewithme.event.dto.EventDto;
import com.github.mrchcat.explorewithme.event.dto.EventShortDto;
import com.github.mrchcat.explorewithme.event.dto.EventUpdateDto;
import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.user.dto.UserShortDto;
import com.github.mrchcat.explorewithme.user.mapper.UserMapper;
import com.github.mrchcat.explorewithme.user.model.User;
import com.github.mrchcat.explorewithme.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final UserService userService;
    private final CategoryService categoryService;
    private final StatHttpClient statHttpClient;

    public Event toEntity(long initiatorId, EventCreateDto ecd) {
        User initiator = userService.getUserById(initiatorId);
        Category category = categoryService.getRawCategoryById(ecd.getCategory());
        return Event.builder()
                .title(ecd.getTitle())
                .annotation(ecd.getAnnotation())
                .description(ecd.getDescription())
                .category(category)
                .eventDate(ecd.getEventDate())
                .location(ecd.getLocation())
                .paid(ecd.getPaid())
                .participantLimit(ecd.getParticipantLimit())
                .requestModeration(ecd.getRequestModeration())
                .initiator(initiator)
                .build();
    }

    public Event updateEntity(Event event, EventUpdateDto updateDto) {
        event.setTitle(updateDto.getTitle());
        event.setAnnotation(updateDto.getAnnotation());
        event.setDescription(updateDto.getDescription());
        long newCategoryId = updateDto.getCategory();
        if (event.getCategory().getId() != newCategoryId) {
            Category newCategory = categoryService.getRawCategoryById(updateDto.getCategory());
            event.setCategory(newCategory);
        }
        event.setEventDate(updateDto.getEventDate());
        event.setLocation(updateDto.getLocation());
        event.setPaid(updateDto.getPaid());
        event.setParticipantLimit(updateDto.getParticipantLimit());
        event.setRequestModeration(updateDto.getRequestModeration());
        return event;
    }

    public EventDto toDto(Event e) {
        CategoryDto categoryDto = CategoryMapper.toDTO(e.getCategory());
        UserShortDto userShortDto = UserMapper.toShortDto(e.getInitiator());
//        TODO добавить количество просмотров
        long views = 0;
//        TODO добавить количество подтвержденных событий
        int confirmedRequests = 0;
        return EventDto.builder()
                .id(e.getId())
                .title(e.getTitle())
                .annotation(e.getAnnotation())
                .description(e.getDescription())
                .category(categoryDto)
                .eventDate(e.getEventDate())
                .location(e.getLocation())
                .paid(e.getPaid())
                .participantLimit(e.getParticipantLimit())
                .requestModeration(e.getRequestModeration())
                .confirmedRequests(confirmedRequests)
                .createdOn(e.getCreatedOn())
                .initiator(userShortDto)
                .publishedOn(e.getPublishedOn())
                .state(e.getState())
                .views(views)
                .build();
    }

    public List<EventDto> toDto(List<Event> eventList) {
        return eventList.stream().map(this::toDto).toList();

//        Map<Long, Tuple> eventMap = mapViewsAndRequests(eventList);
//        return eventList.stream()
//                .map(this::toDto)
//                .peek(dto -> {
//                    long eventId = dto.getId();
//                    Tuple tuple = eventMap.get(eventId);
//                    if (tuple != null) {
//                        dto.setConfirmedRequests(tuple.confirmedRequests);
//                        dto.setViews(tuple.confirmedRequests);
//                    }
//                }).toList();
    }

//    private Map<Long, Tuple> mapViewsAndRequests(List<Event> eventList) {
//        List<Long> eventIds = eventList.stream().map(Event::getId).toList();
//        Map<Long, Tuple> map = new HashMap<>();
////        TODO ВОзврат из базы
//        return map;
//    }
//
//    private record Tuple(Long views, Integer confirmedRequests) {
//    }


    public EventShortDto toShortDto(Event e) {
        CategoryDto categoryDto = CategoryMapper.toDTO(e.getCategory());
        UserShortDto userShortDto = UserMapper.toShortDto(e.getInitiator());
//        TODO добавить количество просмотров
        long views = 0;
//        TODO добавить количество подтвержденных событий
        int confirmedRequests = 0;
        return EventShortDto.builder()
                .id(e.getId())
                .title(e.getTitle())
                .annotation(e.getAnnotation())
                .category(categoryDto)
                .eventDate(e.getEventDate())
                .paid(e.getPaid())
                .confirmedRequests(confirmedRequests)
                .initiator(userShortDto)
                .views(views)
                .build();
    }

    public List<EventShortDto> toShortDto(List<Event> eventList) {
        return eventList.stream().map(this::toShortDto).toList();
    }
}
