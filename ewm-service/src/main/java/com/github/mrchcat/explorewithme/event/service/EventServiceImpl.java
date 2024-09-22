package com.github.mrchcat.explorewithme.event.service;

import com.github.mrchcat.explorewithme.event.dto.EventAdminSearchDto;
import com.github.mrchcat.explorewithme.event.dto.EventAdminUpdateDto;
import com.github.mrchcat.explorewithme.event.dto.EventCreateDto;
import com.github.mrchcat.explorewithme.event.dto.EventDto;
import com.github.mrchcat.explorewithme.event.dto.EventPrivateUpdateDto;
import com.github.mrchcat.explorewithme.event.dto.EventPublicSearchDto;
import com.github.mrchcat.explorewithme.event.dto.EventShortDto;
import com.github.mrchcat.explorewithme.event.mapper.EventMapper;
import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.event.model.EventSortAttribute;
import com.github.mrchcat.explorewithme.event.model.EventState;
import com.github.mrchcat.explorewithme.event.repository.EventRepository;
import com.github.mrchcat.explorewithme.exception.ArgumentNotValidException;
import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
import com.github.mrchcat.explorewithme.exception.RulesViolationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.github.mrchcat.explorewithme.event.model.EventState.CANCELED;
import static com.github.mrchcat.explorewithme.event.model.EventState.PENDING;
import static com.github.mrchcat.explorewithme.event.model.EventState.PUBLISHED;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private static final Duration TIME_GAP_USER = Duration.ofHours(2);
    private static final Duration TIME_GAP_ADMIN = Duration.ofHours(1);
    private static final List<EventState> PERMITTED_STATUS = List.of(CANCELED, PENDING);

    @Override
    public EventDto create(long userId, EventCreateDto createDto) {
        isDateNotTooEarly(createDto.getEventDate(), TIME_GAP_USER);
        Event mappedEvent = eventMapper.toEntity(userId, createDto);
        Event savedEvent = eventRepository.save(mappedEvent);
        log.info("Created event {}", savedEvent);
        return eventMapper.toDto(savedEvent);
    }

    @Override
    public EventDto updateByUser(long userId, long eventId, EventPrivateUpdateDto updateDto) {
        LocalDateTime eventDate = updateDto.getEventDate();
        if (eventDate != null) {
            isDateNotTooEarly(eventDate, TIME_GAP_USER);
        }
        Event oldEvent = getById(eventId);
        isEventHasCorrectStatusToUpdate(oldEvent.getState());
        Event mappedEvent = eventMapper.updateEntityByUser(oldEvent, updateDto);
        Event updatedEvent = eventRepository.save(mappedEvent);
        log.info("User id={} updated event {}", userId, updatedEvent);
        return eventMapper.toDto(updatedEvent);
    }

    @Override
    public EventDto updateByAdmin(long eventId, EventAdminUpdateDto updateDto) {
        LocalDateTime eventDate = updateDto.getEventDate();
        if (eventDate != null) {
            isDateNotTooEarly(eventDate, TIME_GAP_ADMIN);
        }
        Event oldEvent = getById(eventId);
//        EventState oldState = oldEvent.getState();
        Event mappedEvent = eventMapper.updateEntityByAdmin(oldEvent, updateDto);
        Event updatedEvent = eventRepository.save(mappedEvent);
        log.info("Admin updated event {}", updatedEvent);
        return eventMapper.toDto(updatedEvent);
    }

    @Override
    public EventDto getDtoByIdAndUser(long userId, long eventId) {
        Optional<Event> eventOptional = eventRepository.getByIdByUserId(userId, eventId);
        Event event = eventOptional.orElseThrow(() -> {
            String message = String.format("Event with id=%d for user with id=%d was not found", eventId, userId);
            return new ObjectNotFoundException(message);
        });
        return eventMapper.toDto(event);
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
    public List<EventDto> getAllByQuery(EventAdminSearchDto query, Pageable pageable) {
        isCorrectDateOrder(query.getStart(), query.getEnd());
        List<Event> events = eventRepository.getAllEventByQuery(query, pageable);
        return eventMapper.toDto(events);
    }

    @Override
    public List<EventShortDto> getAllShortDtoByUser(long userId, Pageable pageable) {
        List<Event> events = eventRepository.getAllByUserId(userId, pageable);
        return eventMapper.toShortDto(events);
    }

    @Override
    public List<EventShortDto> getAllByQuery(EventPublicSearchDto query,
                                             Pageable pageable,
                                             EventSortAttribute sort,
                                             HttpServletRequest request) {
        isCorrectDateOrder(query.getStart(), query.getEnd());
        List<Event> events = eventRepository.getAllEventByQuery(query, pageable);
        List<EventShortDto> eventShortDtoList = eventMapper.toShortDto(events);

        if (sort != null) {
            var comparator = switch (sort) {
                case VIEWS -> Comparator.comparingLong(EventShortDto::getViews).reversed();
                case EVENT_DATE -> Comparator.comparing(EventShortDto::getEventDate);
            };
            eventShortDtoList.sort(comparator);
        }
//        sendToStatService(request);
        return eventShortDtoList;
    }

    @Override
    public EventDto getDtoById(long eventId, HttpServletRequest request) {
        EventState state = PUBLISHED;
        Event event = eventRepository.getByIdAndStatus(eventId, state).orElseThrow(() -> {
            String message = String.format("Event with id=%d and status=%s was not found", eventId, state);
            return new ObjectNotFoundException(message);
        });
//        sendToStatService(request);
        return eventMapper.toDto(event);
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

}
