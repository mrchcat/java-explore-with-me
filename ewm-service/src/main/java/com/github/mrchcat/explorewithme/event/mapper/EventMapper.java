package com.github.mrchcat.explorewithme.event.mapper;

import com.github.mrchcat.explorewithme.RequestQueryParamDto;
import com.github.mrchcat.explorewithme.RequestStatisticDto;
import com.github.mrchcat.explorewithme.StatHttpClient;
import com.github.mrchcat.explorewithme.category.dto.CategoryDto;
import com.github.mrchcat.explorewithme.category.mapper.CategoryMapper;
import com.github.mrchcat.explorewithme.category.model.Category;
import com.github.mrchcat.explorewithme.category.service.CategoryService;
import com.github.mrchcat.explorewithme.event.dto.EventAdminUpdateDto;
import com.github.mrchcat.explorewithme.event.dto.EventCreateDto;
import com.github.mrchcat.explorewithme.event.dto.EventDto;
import com.github.mrchcat.explorewithme.event.dto.EventPrivateUpdateDto;
import com.github.mrchcat.explorewithme.event.dto.EventShortDto;
import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.event.model.EventAdminStateAction;
import com.github.mrchcat.explorewithme.event.model.EventState;
import com.github.mrchcat.explorewithme.event.model.EventUserStateAction;
import com.github.mrchcat.explorewithme.event.model.Location;
import com.github.mrchcat.explorewithme.exception.DataIntegrityException;
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

import static com.github.mrchcat.explorewithme.event.model.EventAdminStateAction.PUBLISH_EVENT;
import static com.github.mrchcat.explorewithme.event.model.EventAdminStateAction.REJECT_EVENT;
import static com.github.mrchcat.explorewithme.event.model.EventState.CANCELED;
import static com.github.mrchcat.explorewithme.event.model.EventState.PENDING;
import static com.github.mrchcat.explorewithme.event.model.EventState.PUBLISHED;

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
        User initiator = userService.getById(initiatorId);
        Category category = categoryService.getById(ecd.getCategory());
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

    public Event updateEntityByAdmin(Event event, EventAdminUpdateDto updateDto) {
        String title = (updateDto.getTitle());
        if (title != null) {
            event.setTitle(title);
        }
        String annotation = updateDto.getAnnotation();
        if (annotation != null) {
            event.setAnnotation(annotation);
        }
        String description = updateDto.getDescription();
        if (description != null) {
            event.setDescription(description);
        }
        Long categoryId = updateDto.getCategory();
        if ((categoryId != null) && (event.getCategory().getId() != categoryId)) {
            Category newCategory = categoryService.getById(updateDto.getCategory());
            event.setCategory(newCategory);
        }

        LocalDateTime eventDate = updateDto.getEventDate();
        if (eventDate != null) {
            event.setEventDate(eventDate);
        }
        Location location = updateDto.getLocation();
        if (location != null) {
            event.setLocation(location);
        }
        Boolean paid = updateDto.getPaid();
        if (paid != null) {
            event.setPaid(paid);
        }
        Integer participantLimit = updateDto.getParticipantLimit();
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }
        Boolean requestModeration = updateDto.getRequestModeration();
        if (requestModeration != null) {
            event.setRequestModeration(updateDto.getRequestModeration());
        }
        EventAdminStateAction action = updateDto.getStateAction();
        if (action == null) {
            return event;
        }
        EventState oldState = event.getState();
        EventState newState = null;
        if (action.equals(PUBLISH_EVENT)) {
            newState = switch (oldState) {
                case PENDING -> {
                    event.setPublishedOn(LocalDateTime.now());
                    yield PUBLISHED;
                }
                case CANCELED, PUBLISHED -> {
                    String message = String.format("Cannot %s the event because it's not in the right state: %s", action, oldState);
                    throw new DataIntegrityException(message);
                }
            };
        } else if (action.equals(REJECT_EVENT)) {
            newState = switch (oldState) {
                case PENDING -> CANCELED;
                case CANCELED, PUBLISHED -> {
                    String message = String.format("Cannot %s the event because it's not in the right state: %s", action, oldState);
                    throw new DataIntegrityException(message);
                }
            };
        }
        event.setState(newState);
        return event;
    }

    public Event updateEntityByUser(Event event, EventPrivateUpdateDto updateDto) {
        String title = (updateDto.getTitle());
        if (title != null) {
            event.setTitle(title);
        }
        String annotation = updateDto.getAnnotation();
        if (annotation != null) {
            event.setAnnotation(annotation);
        }
        String description = updateDto.getDescription();
        if (description != null) {
            event.setDescription(description);
        }
        Long categoryId = updateDto.getCategory();
        if ((categoryId != null) && (event.getCategory().getId() != categoryId)) {
            Category newCategory = categoryService.getById(updateDto.getCategory());
            event.setCategory(newCategory);
        }
        LocalDateTime eventDate = updateDto.getEventDate();
        if (eventDate != null) {
            event.setEventDate(eventDate);
        }
        Location location = updateDto.getLocation();
        if (location != null) {
            event.setLocation(location);
        }
        Boolean paid = updateDto.getPaid();
        if (paid != null) {
            event.setPaid(paid);
        }
        Integer participantLimit = updateDto.getParticipantLimit();
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }
        Boolean requestModeration = updateDto.getRequestModeration();
        if (requestModeration != null) {
            event.setRequestModeration(updateDto.getRequestModeration());
        }
        EventUserStateAction userAction = updateDto.getStateAction();
        if (userAction != null) {
            EventState newState = switch (userAction) {
                case SEND_TO_REVIEW -> PENDING;
                case CANCEL_REVIEW -> CANCELED;
            };
            event.setState(newState);
        }
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
        LocalDateTime start = event.getCreatedOn();
        String uri = makeUri(event.getId());
        String[] uris = new String[]{uri};
        var uriViewsMap = getRequestFromStat(start, uris);
        return (uriViewsMap.containsKey(uri)) ? uriViewsMap.get(uri) : 0;
    }

    private Map<Long, Long> getEventViews(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, String> idUrisMap = events.stream()
                .map(Event::getId)
                .collect(Collectors.toMap(Function.identity(), this::makeUri));

        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElseThrow(() -> new RuntimeException("Event entity has no creation date"));

        Map<String, Long> uriViewsMap = getRequestFromStat(start, idUrisMap.values().toArray(new String[0]));

        Map<Long, Long> idViewMap = new HashMap<>(uriViewsMap.size());
        for (var entry : idUrisMap.entrySet()) {
            Long id = entry.getKey();
            String uri = entry.getValue();
            idViewMap.put(id, (uriViewsMap.containsKey(uri)) ? uriViewsMap.get(uri) : 0);
        }
        return idViewMap;
    }

    private EventDto toDtoExclViews(Event e) {
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
                .confirmedRequests(e.getConfirmedRequests())
                .participantLimit(e.getParticipantLimit())
                .requestModeration(e.isRequestModeration())
                .createdOn(e.getCreatedOn())
                .initiator(userShortDto)
                .publishedOn(e.getPublishedOn())
                .state(e.getState())
                .build();
    }

    public EventDto toDto(Event e) {
        EventDto eventDto = toDtoExclViews(e);
        long views = getEventViews(e);
        eventDto.setViews(views);
        return eventDto;
    }

    public List<EventDto> toDto(List<Event> events) {
        Map<Long, Long> idViewMap = getEventViews(events);
        return events.stream()
                .map(this::toDtoExclViews)
                .peek(dto -> dto.setViews(idViewMap.get(dto.getId())))
                .collect(Collectors.toList());
    }

    private EventShortDto toShortDtoExclViews(Event e) {
        CategoryDto categoryDto = CategoryMapper.toDTO(e.getCategory());
        UserShortDto userShortDto = UserMapper.toShortDto(e.getInitiator());
        return EventShortDto.builder()
                .id(e.getId())
                .title(e.getTitle())
                .annotation(e.getAnnotation())
                .category(categoryDto)
                .eventDate(e.getEventDate())
                .confirmedRequests(e.getConfirmedRequests())
                .paid(e.isPaid())
                .initiator(userShortDto)
                .build();
    }

    public EventShortDto toShortDto(Event e) {
        EventShortDto eventDto = toShortDtoExclViews(e);
        long views = getEventViews(e);
        eventDto.setViews(views);
        return eventDto;
    }

    public List<EventShortDto> toShortDto(List<Event> events) {
        log.info("вошли в множественный toShortDto c {}", events);
        Map<Long, Long> idViewMap = getEventViews(events);
        log.info("получили сопоставление idViewMap {}", idViewMap);
        return events.stream()
                .map(this::toShortDtoExclViews)
                .peek(dto -> dto.setViews(idViewMap.get(dto.getId())))
                .collect(Collectors.toList());
    }
}
