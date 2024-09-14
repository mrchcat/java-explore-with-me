package com.github.mrchcat.explorewithme.event.service;

import com.github.mrchcat.explorewithme.event.dto.EventCreateDto;
import com.github.mrchcat.explorewithme.event.dto.EventDto;
import com.github.mrchcat.explorewithme.event.dto.EventShortDto;
import com.github.mrchcat.explorewithme.event.dto.EventUpdateDto;
import com.github.mrchcat.explorewithme.event.mapper.EventMapper;
import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.event.model.EventState;
import com.github.mrchcat.explorewithme.event.model.EventStateAction;
import com.github.mrchcat.explorewithme.event.repository.EventRepository;
import com.github.mrchcat.explorewithme.exception.DataIntegrityException;
import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
import com.github.mrchcat.explorewithme.user.service.UserService;
import com.github.mrchcat.explorewithme.validator.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Optional;

import static com.github.mrchcat.explorewithme.event.model.EventState.CANCELED;
import static com.github.mrchcat.explorewithme.event.model.EventState.PENDING;
import static com.github.mrchcat.explorewithme.event.model.EventState.PUBLISHED;
import static com.github.mrchcat.explorewithme.event.model.EventStateAction.CANCEL_REVIEW;
import static com.github.mrchcat.explorewithme.event.model.EventStateAction.PUBLISH_EVENT;
import static com.github.mrchcat.explorewithme.event.model.EventStateAction.SEND_TO_REVIEW;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final Validator validator;
    private final EventMapper eventMapper;

    @Override
    public EventDto createEvent(long userId, EventCreateDto createDto) {
        validator.isDateNotTooEarlyUser(createDto.getEventDate());
        Event mappedEvent = eventMapper.toEntity(userId, createDto);
        Event savedEvent = eventRepository.save(mappedEvent);
        return eventMapper.toDto(savedEvent);
    }

    @Override
    public EventDto updateEventByUser(long userId, long eventId, EventUpdateDto updateDto) {
        validator.isDateNotTooEarlyUser(updateDto.getEventDate());
        Event oldEvent = getEventById(eventId);
        validator.isEventHasCorrectStatusToUpdate(oldEvent.getState());
        Event mappedEvent = eventMapper.updateEntity(oldEvent, updateDto);

        EventStateAction statusAction = updateDto.getStateAction();
        EventState newState;
        if (statusAction == null || statusAction.equals(SEND_TO_REVIEW)) {
            newState = PENDING;
        } else {
            newState = CANCELED;
        }
        mappedEvent.setState(newState);

        Event updatedEvent = eventRepository.save(mappedEvent);
        return eventMapper.toDto(updatedEvent);
    }

    @Override
    public EventDto updateEventByAdmin(long eventId, EventUpdateDto updateDto) {
        validator.isDateNotTooEarlyAdmin(updateDto.getEventDate());
        Event oldEvent = getEventById(eventId);
        Event mappedEvent = eventMapper.updateEntity(oldEvent, updateDto);
        EventState newState = getNewState(oldEvent, updateDto);
        mappedEvent.setState(newState);
        Event updatedEvent = eventRepository.save(mappedEvent);
        return eventMapper.toDto(updatedEvent);
    }

    private EventState getNewState(Event oldEvent, EventUpdateDto updateDto) {
        EventState oldState = oldEvent.getState();
        EventStateAction action = updateDto.getStateAction();
        if (action.equals(PUBLISH_EVENT) && (oldState.equals(CANCELED) || oldState.equals(PUBLISHED))) {
            String message = String.format("Cannot %s the event because it's not in the right state: %s", action, oldState);
            throw new DataIntegrityException(message);
        }
        return switch (action) {
            case CANCEL_REVIEW -> CANCELED;
            case SEND_TO_REVIEW -> PENDING;
            case PUBLISH_EVENT -> PUBLISHED;
        };
    }

    @Override
    public List<EventShortDto> getAllShortEventDtoByUser(long userId, long from, long size) {
        validator.isUserIdExists(userId);
        List<Event> events = eventRepository.getAllEventsByUserId(userId, from, size);
        return eventMapper.toShortDto(events);
    }

    @Override
    public EventDto getEventDtoByIdByUser(long userId, long eventId) {
        validator.isUserIdExists(userId);
        Optional<Event> eventOptional = eventRepository.getEventByIdByUserId(userId, eventId);
        Event event = eventOptional.orElseThrow(() -> {
            String message = String.format("Event with id=%d for user with id=%d was not found", eventId, userId);
            return new ObjectNotFoundException(message);
        });
        return eventMapper.toDto(event);
    }

    @Override
    public Event getEventById(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> {
            String message = String.format("Event with id=%d was not found", eventId);
            return new ObjectNotFoundException(message);
        });
    }
}
