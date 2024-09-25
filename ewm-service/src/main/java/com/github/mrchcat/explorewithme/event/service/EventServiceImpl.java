package com.github.mrchcat.explorewithme.event.service;

import com.github.mrchcat.explorewithme.category.model.Category;
import com.github.mrchcat.explorewithme.category.repository.CategoryRepository;
import com.github.mrchcat.explorewithme.event.dto.EventAdminSearchDto;
import com.github.mrchcat.explorewithme.event.dto.EventAdminUpdateDto;
import com.github.mrchcat.explorewithme.event.dto.EventCreateDto;
import com.github.mrchcat.explorewithme.event.dto.EventDto;
import com.github.mrchcat.explorewithme.event.dto.EventPrivateUpdateDto;
import com.github.mrchcat.explorewithme.event.dto.EventPublicSearchDto;
import com.github.mrchcat.explorewithme.event.dto.EventShortDto;
import com.github.mrchcat.explorewithme.event.dto.EventUpdateDto;
import com.github.mrchcat.explorewithme.event.mapper.EventMapper;
import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.event.model.EventAdminStateAction;
import com.github.mrchcat.explorewithme.event.model.EventState;
import com.github.mrchcat.explorewithme.event.model.EventUserStateAction;
import com.github.mrchcat.explorewithme.event.model.Location;
import com.github.mrchcat.explorewithme.event.repository.EventRepository;
import com.github.mrchcat.explorewithme.exception.DataIntegrityException;
import com.github.mrchcat.explorewithme.exception.NotFoundException;
import com.github.mrchcat.explorewithme.exception.RulesViolationException;
import com.github.mrchcat.explorewithme.user.model.User;
import com.github.mrchcat.explorewithme.user.repository.UserRepository;
import com.github.mrchcat.explorewithme.utils.participant.service.Participants;
import com.github.mrchcat.explorewithme.utils.views.Views;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final Views views;
    private final Participants participants;
    private static final List<EventState> PERMITTED_STATUS = List.of(CANCELED, PENDING);

    @Transactional
    @Override
    public EventDto create(long userId, EventCreateDto createDto) {
        User initiator = getUserById(userId);
        Category category = getCategoryById(createDto.getCategory());
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
        return EventMapper.toDto(event, views.getEventViews(savedEvent),participants.getEventParticipants(savedEvent));
    }

    @Transactional
    @Override
    public EventDto updateByUser(long userId, long eventId, EventPrivateUpdateDto updateDto) {
        Event event = getById(eventId);
        isEventHasCorrectStatusToUpdate(event.getState());
        updateFields(event, updateDto, updateDto.getEventDate());
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
        return EventMapper.toDto(event, views.getEventViews(updatedEvent),participants.getEventParticipants(updatedEvent));
    }

    @Transactional
    @Override
    public EventDto updateByAdmin(long eventId, EventAdminUpdateDto updateDto) {
        Event event = getById(eventId);
        updateFields(event, updateDto, updateDto.getEventDate());
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
                        String message = "Cannot " + action + " the event because it's not in the right state: " + oldState;
                        throw new DataIntegrityException(message);
                    }
                };
            } else if (action.equals(REJECT_EVENT)) {
                newState = switch (oldState) {
                    case PENDING -> CANCELED;
                    case CANCELED, PUBLISHED -> {
                        String message = "Cannot " + action + " the event because it's not in the right state: " + oldState;
                        throw new DataIntegrityException(message);
                    }
                };
            }
            event.setState(newState);
        }
        Event updatedEvent = eventRepository.save(event);
        log.info("Admin updated event {}", updatedEvent);
        return EventMapper.toDto(updatedEvent, views.getEventViews(updatedEvent),participants.getEventParticipants(updatedEvent));

    }

    @Override
    public EventDto getDtoByIdAndUser(long userId, long eventId) {
        Optional<Event> eventOptional = eventRepository.getByIdByUserId(userId, eventId);
        Event event = eventOptional.orElseThrow(() -> {
            String message = "Event with id=" + eventId + " for user with id=" + userId + " was not found";
            return new NotFoundException(message);
        });
        return EventMapper.toDto(event, views.getEventViews(event),participants.getEventParticipants(event));
    }

    public Event getById(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> {
            String message = "Event with id=" + eventId + " was not found";
            return new NotFoundException(message);
        });
    }

    @Override
    public List<EventDto> getAllByQuery(EventAdminSearchDto query) {
        List<Event> events = eventRepository.getAllEventByQuery(query);
        return EventMapper.toDto(events, views.getEventViews(events),participants.getEventParticipants(events));
    }

    @Override
    public List<EventShortDto> getAllShortDtoByUser(long userId, Pageable pageable) {
        List<Event> events = eventRepository.getAllByUserId(userId, pageable);
        return EventMapper.toShortDto(events, views.getEventViews(events),participants.getEventParticipants(events));
    }

    @Override
    public List<EventShortDto> getAllByQuery(EventPublicSearchDto query) {
        List<Event> events = eventRepository.getAllEventByQuery(query);
        List<EventShortDto> eventShortDtoList = EventMapper.toShortDto(events, views.getEventViews(events),participants.getEventParticipants(events));
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
            String message = "Event with id=" + eventId + " and status=" + state + " was not found";
            return new NotFoundException(message);
        });
        return EventMapper.toDto(event, views.getEventViews(event),participants.getEventParticipants(event));
    }

    private void isEventHasCorrectStatusToUpdate(EventState state) {
        for (EventState allowed : PERMITTED_STATUS) {
            if (state.equals(allowed)) {
                return;
            }
        }
        String message = "Only " + PERMITTED_STATUS + " can be changed";
        throw new RulesViolationException(message);
    }

     private void updateFields(Event event, EventUpdateDto updateDto, LocalDateTime eventDate) {

        if (eventDate != null) {
            event.setEventDate(eventDate);
        }

        Long categoryId = updateDto.getCategory();
        if ((categoryId != null) && (event.getCategory().getId() != categoryId)) {
            Category category = getCategoryById(updateDto.getCategory());
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
    }

    private User getUserById(long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.orElseThrow(() -> {
            String message = "User with id=" + userId + " was not found";
            return new NotFoundException(message);
        });
    }

    private Category getCategoryById(long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        return categoryOptional.orElseThrow(() -> {
            String message = "Category with id=" + categoryId + " was not found";
            return new NotFoundException(message);
        });
    }
}
