package com.github.mrchcat.explorewithme.event.mapper;

import com.github.mrchcat.explorewithme.RequestQueryParamDto;
import com.github.mrchcat.explorewithme.RequestStatisticDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventMapper {
    private final UserService userService;
    private final CategoryService categoryService;
    private final StatHttpClient statHttpClient;
    private static final boolean IS_UNIQUE_VIEWS = true;
    private static final String PUBLIC_VIEW_URI = "/events";

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

    private String makeUri(long id) {
        return PUBLIC_VIEW_URI + "/" + id;
    }

    private Map<String, Long> getRequestFromStat(LocalDateTime start, String[] uris) {
        log.info("зашли в getRequestFromStat c параметрами {} {}", start, Arrays.toString(uris));
        var request = RequestQueryParamDto.builder()
                .start(start)
                .end(LocalDateTime.now())
                .uris(uris)
                .unique(IS_UNIQUE_VIEWS)
                .build();
        log.info("Создали request {}", request);
        try {
            List<RequestStatisticDto> answer = statHttpClient.getRequestStatistic(request);
            log.info("Ответ {}", answer);
            return answer.stream()
                    .collect(Collectors.toMap(RequestStatisticDto::getUri, RequestStatisticDto::getHits));
        } catch (IOException ex) {
            log.error("Stat service returned an exception for request {}", request);
            return Stream.of(uris).collect(Collectors.toMap(Function.identity(), u -> 0L));
        }
    }

    private long getEventViews(Event event) {
        log.info("зашли в getEventViews c {}", event);
        LocalDateTime start = event.getCreatedOn();
        log.info("start= {}", start);
        String uri = makeUri(event.getId());
        String[] uris = new String[]{uri};
        log.info("uris= {}", Arrays.toString(uris));
        var uriViewsMap = getRequestFromStat(start, uris);
        return (uriViewsMap.containsKey(uri)) ? uriViewsMap.get(uri) : 0;
    }

    private Map<Long, Long> getEventViews(List<Event> events) {
        log.info("вошли в getEventViews c {}", events);
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, String> idUrisMap = events.stream()
                .map(Event::getId)
                .collect(Collectors.toMap(Function.identity(), this::makeUri));
        log.info("IdUrisMap={}", idUrisMap);

        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElseThrow(() -> new RuntimeException("Event entity has no creation date"));
        log.info("start={}", start);

        Map<String, Long> uriViewsMap = getRequestFromStat(start, idUrisMap.values().toArray(new String[0]));
        log.info("uriViewsMap={}", uriViewsMap);

        Map<Long, Long> idViewMap = new HashMap<>(uriViewsMap.size());
        for (var entry : idUrisMap.entrySet()) {
            Long id = entry.getKey();
            String uri = entry.getValue();
            idViewMap.put(id, (uriViewsMap.containsKey(uri)) ? uriViewsMap.get(uri) : 0);
        }
        log.info("idViewMap={}", idViewMap);
        return idViewMap;
    }

    private EventDto toDtoExclViewsRequests(Event e) {
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
                .paid(e.getPaid())
                .participantLimit(e.getParticipantLimit())
                .requestModeration(e.getRequestModeration())
                .createdOn(e.getCreatedOn())
                .initiator(userShortDto)
                .publishedOn(e.getPublishedOn())
                .state(e.getState())
                .build();
    }

    public EventDto toDto(Event e) {
        log.info("зашли в toDto  ссобытием {}", e);
        EventDto eventDto = toDtoExclViewsRequests(e);
        log.info("сделали полуфабрикат toDtoExclViewsRequests {}", eventDto);
        long views = getEventViews(e);
        log.info("views= {}", views);
        eventDto.setViews(views);
        //        TODO добавить количество подтвержденных событий
        int confirmedRequests = 0;
        eventDto.setConfirmedRequests(confirmedRequests);
        return eventDto;
    }

    public List<EventDto> toDto(List<Event> events) {
        Map<Long, Long> idViewMap = getEventViews(events);
        // TODO  добавить количество подтвержденных событий
        Map<Long, Long> idRequestMap;
        return events.stream()
                .map(this::toDtoExclViewsRequests)
                .peek(dto -> dto.setViews(idViewMap.get(dto.getId())))
                .collect(Collectors.toList());
    }

    private EventShortDto toShortDtoExclViewsRequests(Event e) {
        CategoryDto categoryDto = CategoryMapper.toDTO(e.getCategory());
        UserShortDto userShortDto = UserMapper.toShortDto(e.getInitiator());
        return EventShortDto.builder()
                .id(e.getId())
                .title(e.getTitle())
                .annotation(e.getAnnotation())
                .category(categoryDto)
                .eventDate(e.getEventDate())
                .paid(e.getPaid())
                .initiator(userShortDto)
                .build();
    }

    public EventShortDto toShortDto(Event e) {
        EventShortDto eventDto = toShortDtoExclViewsRequests(e);
        long views = getEventViews(e);
        eventDto.setViews(views);
        //        TODO добавить количество подтвержденных событий
        int confirmedRequests = 0;
        eventDto.setConfirmedRequests(confirmedRequests);
        return eventDto;
    }

    public List<EventShortDto> toShortDto(List<Event> events) {
        log.info("вошли в множественный toShortDto c {}", events);
        Map<Long, Long> idViewMap = getEventViews(events);
        log.info("получили сопоставление idViewMap {}", idViewMap);
        // TODO  добавить количество подтвержденных событий
        Map<Long, Long> idRequestMap;
        return events.stream()
                .map(this::toShortDtoExclViewsRequests)
                .peek(dto -> dto.setViews(idViewMap.get(dto.getId())))
                .collect(Collectors.toList());
    }
}

//    List<Long> getEventIds(List<Event> events) {
//        return events.stream().map(Event::getId).toList();
//    }

//    private long getIdFromUri(String uri) {
//        String prefix = PUBLIC_VIEW_URI + "/";
//        int num = prefix.length();
//        return Long.parseLong(uri.substring(num));
//    }