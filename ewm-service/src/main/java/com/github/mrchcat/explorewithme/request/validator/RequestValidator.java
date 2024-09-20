package com.github.mrchcat.explorewithme.request.validator;

import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.event.repository.EventRepository;
import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
import com.github.mrchcat.explorewithme.exception.RulesViolationException;
import com.github.mrchcat.explorewithme.request.model.Request;
import com.github.mrchcat.explorewithme.request.model.RequestStatus;
import com.github.mrchcat.explorewithme.request.repository.RequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.github.mrchcat.explorewithme.event.model.EventState.PUBLISHED;

@Component
@AllArgsConstructor
public class RequestValidator {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;


    public void isPending(Request request) {
        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            String message = String.format("Request status must be Pending for request %s", request);
            throw new RulesViolationException(message);
        }
    }

    public boolean isInfiniteLimit(Event event) {
        return event.getParticipantLimit() == 0;
    }

    public void isFreeLimitEnough(Event event) {
        if (!isInfiniteLimit(event) && (event.getParticipantLimit() - event.getConfirmedRequests() <= 0)) {
            String message = "The participant limit has been reached";
            throw new RulesViolationException(message);
        }
    }


    public void isRequestCorrespondEvent(Event event, List<Request> requests) {
        for (Request r : requests) {
            if (r.getEvent().getId() != event.getId()) {
                String message = String.format("Request id=%s does not correspond event id=%s", r, event);
                throw new ObjectNotFoundException(message);
            }
        }
    }

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

}
