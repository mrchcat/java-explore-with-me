package com.github.mrchcat.explorewithme.event.service;

import com.github.mrchcat.explorewithme.RequestCreateDto;
import com.github.mrchcat.explorewithme.StatHttpClient;
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
import com.github.mrchcat.explorewithme.event.validator.EventValidator;
import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
import com.github.mrchcat.explorewithme.user.validator.UserValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.github.mrchcat.explorewithme.event.model.EventState.PUBLISHED;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserValidator userValidator;
    private final EventValidator eventValidator;
    private final EventMapper eventMapper;
    private final StatHttpClient statHttpClient;
    @Value("${app.name}")
    private String appName;

    @Override
    public EventDto create(long userId, EventCreateDto createDto) {
        eventValidator.isDateNotTooEarlyUser(createDto.getEventDate());
        Event mappedEvent = eventMapper.toEntity(userId, createDto);
        Event savedEvent = eventRepository.save(mappedEvent);
        log.info("Created event {}", savedEvent);
        return eventMapper.toDto(savedEvent);
    }

    @Override
    public EventDto updateByUser(long userId, long eventId, EventPrivateUpdateDto updateDto) {
        if (updateDto.getEventDate() != null) {
            eventValidator.isDateNotTooEarlyUser(updateDto.getEventDate());
        }
        Event oldEvent = getById(eventId);
        eventValidator.isEventHasCorrectStatusToUpdate(oldEvent.getState());
        Event mappedEvent = eventMapper.updateEntityByUser(oldEvent, updateDto);
//        EventUserStateAction statusAction = updateDto.getStateAction();
//        EventState newState;
//        if (statusAction != null) {
//            newState = switch (statusAction) {
//                case SEND_TO_REVIEW -> PENDING;
//                case CANCEL_REVIEW -> CANCELED;
//            };
//            mappedEvent.setState(newState);
//        }
        Event updatedEvent = eventRepository.save(mappedEvent);
        log.info("User id={} updated event {}", userId, updatedEvent);
        return eventMapper.toDto(updatedEvent);
    }

    @Override
    public EventDto updateByAdmin(long eventId, EventAdminUpdateDto updateDto) {
        if (updateDto.getEventDate() != null) {
            eventValidator.isDateNotTooEarlyAdmin(updateDto.getEventDate());
        }
        Event oldEvent = getById(eventId);
        EventState oldState = oldEvent.getState();
        Event mappedEvent = eventMapper.updateEntityByAdmin(oldEvent, updateDto);
//        EventState newState = getNewState(oldEvent, updateDto);
//        if(newState.equals(PUBLISHED)){
//            switch (oldState){
//                case PENDING -> mappedEvent.setPublishedOn(LocalDateTime.now());
//                case CANCELED -> throw new RulesViolationException("Cancelled event can not be published");
//            }
//        }
//        mappedEvent.setState(newState);
        Event updatedEvent = eventRepository.save(mappedEvent);
        log.info("Admin updated event {}", updatedEvent);
        return eventMapper.toDto(updatedEvent);
    }

//    private EventState getNewState(Event oldEvent, EventAdminUpdateDto updateDto) {
//        EventState oldState = oldEvent.getState();
//        EventAdminStateAction action = updateDto.getStateAction();
//        if (action == null) {
//            return oldState;
//        }
//        if (action.equals(PUBLISH_EVENT) && (oldState.equals(CANCELED) || oldState.equals(PUBLISHED))) {
//            String message = String.format("Cannot %s the event because it's not in the right state: %s", action, oldState);
//            throw new DataIntegrityException(message);
//        }
//        return switch (action) {
//            case CANCEL_REVIEW -> CANCELED;
//            case SEND_TO_REVIEW -> PENDING;
//            case PUBLISH_EVENT -> PUBLISHED;
//        };
//    }

    @Override
    public EventDto getDtoByIdAndUser(long userId, long eventId) {
        userValidator.isUserIdExists(userId);
        log.info("Validator отработал успешно");
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
        eventValidator.isCorrectDateOrder(query.getStart(), query.getEnd());
        List<Event> events = eventRepository.getAllEventByQuery(query, pageable);
        return eventMapper.toDto(events);
    }

    @Override
    public List<EventShortDto> getAllShortDtoByUser(long userId, Pageable pageable) {
        userValidator.isUserIdExists(userId);
        List<Event> events = eventRepository.getAllByUserId(userId, pageable);
        return eventMapper.toShortDto(events);
    }

    @Override
    public List<EventShortDto> getAllByQuery(EventPublicSearchDto query,
                                             Pageable pageable,
                                             EventSortAttribute sort,
                                             HttpServletRequest request) {
        log.info("зашли в getAllByQuery с параметрами query={} pageable={} sort={} request={}",
                query, pageable, sort, request);
        eventValidator.isCorrectDateOrder(query.getStart(), query.getEnd());
        List<Event> events = eventRepository.getAllEventByQuery(query, pageable);
        log.info("сделали запрос в БД и получили ответ {}", events);
        List<EventShortDto> eventShortDtoList = eventMapper.toShortDto(events);

        if (sort != null) {
            var comparator = switch (sort) {
                case VIEWS -> Comparator.comparingLong(EventShortDto::getViews).reversed();
                case EVENT_DATE -> Comparator.comparing(EventShortDto::getEventDate);
            };
            eventShortDtoList.sort(comparator);
        }
        sendToStatService(request);
        return eventShortDtoList;
    }

    @Override
    public EventDto getDtoById(long eventId, HttpServletRequest request) {
        EventState state = PUBLISHED;
        Event event = eventRepository.getByIdAndStatus(eventId, state).orElseThrow(() -> {
            String message = String.format("Event with id=%d and status=%s was not found", eventId, state);
            return new ObjectNotFoundException(message);
        });
        sendToStatService(request);
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
//        validator.checkParticipantLimit(event);
        int oldConfirmedRequests = event.getConfirmedRequests();
        event.setConfirmedRequests(oldConfirmedRequests + number);
        log.info("Event {} was incremented by {}", event, number);
        eventRepository.save(event);
    }

    @Override
    public void incrementConfirmedRequest(Event event) {
        incrementConfirmedRequest(event, 1);
    }

    private void sendToStatService(HttpServletRequest request) {
        log.info("зашли в sendToStatService {}", request);
        String remoteAddress = request.getRemoteAddr();
        InetAddress ip = null;
        try {
            ip = InetAddress.getByName(remoteAddress);
        } catch (UnknownHostException e) {
            log.error("RemoteAddress {} can not be converted to InetAdress", remoteAddress);
        }
        RequestCreateDto statRequest = RequestCreateDto.builder()
                .app(appName)
                .uri(request.getRequestURI())
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
        log.info("создали запрос {}", statRequest);
        statHttpClient.addRequest(statRequest);
    }
}
