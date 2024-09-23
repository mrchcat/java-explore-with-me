package com.github.mrchcat.explorewithme.event.service;

import com.github.mrchcat.explorewithme.RequestQueryParamDto;
import com.github.mrchcat.explorewithme.RequestStatisticDto;
import com.github.mrchcat.explorewithme.StatHttpClient;
import com.github.mrchcat.explorewithme.category.model.Category;
import com.github.mrchcat.explorewithme.category.service.CategoryService;
import com.github.mrchcat.explorewithme.event.dto.EventAdminSearchDto;
import com.github.mrchcat.explorewithme.event.dto.EventAdminUpdateDto;
import com.github.mrchcat.explorewithme.event.dto.EventCreateDto;
import com.github.mrchcat.explorewithme.event.dto.EventDto;
import com.github.mrchcat.explorewithme.event.dto.EventPrivateUpdateDto;
import com.github.mrchcat.explorewithme.event.dto.EventPublicSearchDto;
import com.github.mrchcat.explorewithme.event.dto.EventShortDto;
import com.github.mrchcat.explorewithme.event.mapper.EventMapper;
import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.event.model.EventAdminStateAction;
import com.github.mrchcat.explorewithme.event.model.EventState;
import com.github.mrchcat.explorewithme.event.model.EventUserStateAction;
import com.github.mrchcat.explorewithme.event.model.Location;
import com.github.mrchcat.explorewithme.event.repository.EventRepository;
import com.github.mrchcat.explorewithme.exception.ArgumentNotValidException;
import com.github.mrchcat.explorewithme.exception.DataIntegrityException;
import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
import com.github.mrchcat.explorewithme.exception.RulesViolationException;
import com.github.mrchcat.explorewithme.user.model.User;
import com.github.mrchcat.explorewithme.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.mrchcat.explorewithme.event.model.EventAdminStateAction.PUBLISH_EVENT;
import static com.github.mrchcat.explorewithme.event.model.EventAdminStateAction.REJECT_EVENT;
import static com.github.mrchcat.explorewithme.event.model.EventState.CANCELED;
import static com.github.mrchcat.explorewithme.event.model.EventState.PENDING;
import static com.github.mrchcat.explorewithme.event.model.EventState.PUBLISHED;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final StatHttpClient statHttpClient;
    private static final Duration TIME_GAP_USER = Duration.ofHours(2);
    private static final Duration TIME_GAP_ADMIN = Duration.ofHours(1);
    private static final List<EventState> PERMITTED_STATUS = List.of(CANCELED, PENDING);
    private static final boolean IS_UNIQUE_VIEWS = true;
    private static final String PUBLIC_VIEW_URI = "/events";

    @Override
    public EventDto create(long userId, EventCreateDto createDto) {
        isDateNotTooEarly(createDto.getEventDate(), TIME_GAP_USER);
        User initiator = userService.getById(userId);
        Category category = categoryService.getById(createDto.getCategory());
        Event event = Event.builder()
                .title(createDto.getTitle())
                .annotation(createDto.getAnnotation())
                .description(createDto.getDescription())
                .category(category)
                .eventDate(createDto.getEventDate())
                .location(createDto.getLocation())
                .paid(createDto.getPaid())
                .participantLimit(createDto.getParticipantLimit())
                .requestModeration(createDto.getRequestModeration())
                .initiator(initiator)
                .build();
        Event savedEvent = eventRepository.save(event);
        log.info("Created event {}", savedEvent);
        long views = getEventViews(savedEvent);
        return EventMapper.toDto(savedEvent, views);
    }

    @Override
    public EventDto updateByUser(long userId, long eventId, EventPrivateUpdateDto updateDto) {
        Event event = getById(eventId);
        isEventHasCorrectStatusToUpdate(event.getState());

        LocalDateTime eventDate = updateDto.getEventDate();
        if (eventDate != null) {
            isDateNotTooEarly(eventDate, TIME_GAP_USER);
            event.setEventDate(eventDate);
        }

        Long categoryId = updateDto.getCategory();
        if ((categoryId != null) && (event.getCategory().getId() != categoryId)) {
            Category category = categoryService.getById(updateDto.getCategory());
            event.setCategory(category);
        }

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

        Event updatedEvent = eventRepository.save(event);
        log.info("User id={} updated event {}", userId, updatedEvent);
        long views = getEventViews(updatedEvent);
        return EventMapper.toDto(updatedEvent, views);
    }

    @Override
    public EventDto updateByAdmin(long eventId, EventAdminUpdateDto updateDto) {
        Event event = getById(eventId);

        LocalDateTime eventDate = updateDto.getEventDate();
        if (eventDate != null) {
            isDateNotTooEarly(eventDate, TIME_GAP_ADMIN);
            event.setEventDate(eventDate);
        }

        Long categoryId = updateDto.getCategory();
        if ((categoryId != null) && (event.getCategory().getId() != categoryId)) {
            Category category = categoryService.getById(updateDto.getCategory());
            event.setCategory(category);
        }
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
        if (action != null) {
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
        }
        Event updatedEvent = eventRepository.save(event);
        log.info("Admin updated event {}", updatedEvent);
        long views = getEventViews(updatedEvent);
        return EventMapper.toDto(updatedEvent, views);
    }

    @Override
    public EventDto getDtoByIdAndUser(long userId, long eventId) {
        Optional<Event> eventOptional = eventRepository.getByIdByUserId(userId, eventId);
        Event event = eventOptional.orElseThrow(() -> {
            String message = String.format("Event with id=%d for user with id=%d was not found", eventId, userId);
            return new ObjectNotFoundException(message);
        });
        long views = getEventViews(event);
        return EventMapper.toDto(event, views);
    }

    @Override
    public Event getById(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> {
            String message = String.format("Event with id=%d was not found", eventId);
            return new ObjectNotFoundException(message);
        });
    }

    @Override
    public Event getByIdAndInitiator(long userId, long eventId) {
        return eventRepository.getByIdAndInitiator(userId, eventId).orElseThrow(() -> {
            String message = String.format("Event with id=%d with initiator %d was not found", eventId, userId);
            return new ObjectNotFoundException(message);
        });
    }

    @Override
    public List<Event> getById(List<Long> eventIds) {
        return eventRepository.findAllById(eventIds);
    }

    @Override
    public List<EventDto> getAllByQuery(EventAdminSearchDto query) {
        isCorrectDateOrder(query.getStart(), query.getEnd());
        List<Event> events = eventRepository.getAllEventByQuery(query);
        Map<Long, Long> idViewMap = getEventViews(events);
        return EventMapper.toDto(events, idViewMap);
    }

    @Override
    public List<EventShortDto> getAllShortDtoByUser(long userId, Pageable pageable) {
        List<Event> events = eventRepository.getAllByUserId(userId, pageable);
        Map<Long, Long> idViewMap = getEventViews(events);
        return EventMapper.toShortDto(events, idViewMap);
    }

    @Override
    public List<EventShortDto> getAllByQuery(EventPublicSearchDto query) {
        isCorrectDateOrder(query.getStart(), query.getEnd());
        List<Event> events = eventRepository.getAllEventByQuery(query);
        Map<Long, Long> idViewMap = getEventViews(events);
        List<EventShortDto> eventShortDtoList = EventMapper.toShortDto(events, idViewMap);
        var sort = query.getEventSortAttribute();
        if (sort != null) {
            var comparator = switch (sort) {
                case VIEWS -> Comparator.comparingLong(EventShortDto::getViews).reversed();
                case EVENT_DATE -> Comparator.comparing(EventShortDto::getEventDate);
            };
            eventShortDtoList.sort(comparator);
        }
        return eventShortDtoList;
    }

    @Override
    public EventDto getDtoById(long eventId) {
        EventState state = PUBLISHED;
        Event event = eventRepository.getByIdAndStatus(eventId, state).orElseThrow(() -> {
            String message = String.format("Event with id=%d and status=%s was not found", eventId, state);
            return new ObjectNotFoundException(message);
        });
        long views = getEventViews(event);
        return EventMapper.toDto(event, views);
    }

    @Override
    public void decrementConfirmedRequest(Event event) {
        int oldConfirmedRequests = event.getConfirmedRequests();
        event.setConfirmedRequests(oldConfirmedRequests - 1);
        log.info("Event {} was decremented by 1", event);
        eventRepository.save(event);
    }

    @Override
    public void incrementConfirmedRequest(Event event, int number) {
        int oldConfirmedRequests = event.getConfirmedRequests();
        event.setConfirmedRequests(oldConfirmedRequests + number);
        log.info("Event {} was incremented by {}", event, number);
        eventRepository.save(event);
    }

    @Override
    public void incrementConfirmedRequest(Event event) {
        incrementConfirmedRequest(event, 1);
    }

    private void isDateNotTooEarly(LocalDateTime eventDate, Duration gap) {
        LocalDateTime earliestPossibleTime = LocalDateTime.now().plus(gap);
        if (eventDate.isBefore(earliestPossibleTime)) {
            String message = String.format("Start of event must be not earlier than %d hours before now",
                    gap.getSeconds() / 60 / 60);
            throw new ArgumentNotValidException(message);
        }
    }

    private void isEventHasCorrectStatusToUpdate(EventState state) {
        for (EventState allowed : PERMITTED_STATUS) {
            if (state.equals(allowed)) {
                return;
            }
        }
        String message = String.format("Only %s can be changed", PERMITTED_STATUS);
        throw new RulesViolationException(message);
    }

    private void isCorrectDateOrder(LocalDateTime start, LocalDateTime finish) {
        if (start != null && finish != null && finish.isBefore(start)) {
            String message = String.format("The dates violate order: %s must be before %s", start, finish);
            throw new ArgumentNotValidException(message);
        }
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

    public long getEventViews(Event event) {
        LocalDateTime start = event.getCreatedOn();
        String uri = makeUri(event.getId());
        String[] uris = new String[]{uri};
        var uriViewsMap = getRequestFromStat(start, uris);
        return (uriViewsMap.containsKey(uri)) ? uriViewsMap.get(uri) : 0;
    }

    public Map<Long, Long> getEventViews(List<Event> events) {
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


}
