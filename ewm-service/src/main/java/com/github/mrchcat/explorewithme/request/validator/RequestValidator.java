package com.github.mrchcat.explorewithme.request.validator;

import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.event.repository.EventRepository;
import com.github.mrchcat.explorewithme.exception.RulesViolationException;
import com.github.mrchcat.explorewithme.request.repository.RequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import static com.github.mrchcat.explorewithme.event.model.EventState.PUBLISHED;

@Component
@AllArgsConstructor
public class RequestValidator {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;

    public void isRequestExists(long userId, long eventId) {
        if (requestRepository.exists(userId, eventId)) {
            String message = String.format("Event request from user id=%d for event id=%d already exists",
                    userId, eventId);
            throw new RulesViolationException(message);
        }
    }

    public void isRequestForOwnEvent(long userId, Event event) {
        if (event.getInitiator().getId() == userId) {
            String message = String.format("User id=%d can not make request for it's own event id=%d",
                    userId, event.getId());
            throw new RulesViolationException(message);
        }
    }

    public void isPublishedEvent(Event event) {
        if (!event.getState().equals(PUBLISHED)) {
            String message = "Requested event must be published";
            throw new RulesViolationException(message);
        }
    }

    public void isParticipantLimitExceeded(Event event) {
        if (event.getParticipantLimit() <= 0) {
            String message = String.format("Participant limit for event id=%d is exceeded", event.getId());
            throw new RulesViolationException(message);
        }
    }
}
