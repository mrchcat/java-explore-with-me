package com.github.mrchcat.explorewithme.event.validator;

import com.github.mrchcat.explorewithme.event.model.EventState;
import com.github.mrchcat.explorewithme.event.repository.EventRepository;
import com.github.mrchcat.explorewithme.exception.ArgumentNotValidException;
import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
import com.github.mrchcat.explorewithme.exception.RulesViolationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.github.mrchcat.explorewithme.event.model.EventState.CANCELED;
import static com.github.mrchcat.explorewithme.event.model.EventState.PENDING;

@Component
@AllArgsConstructor
public class EventValidator {
    private final EventRepository eventRepository;
    private static final Duration TIME_GAP_USER = Duration.ofHours(2);
    private static final Duration TIME_GAP_ADMIN = Duration.ofHours(1);
    private static final List<EventState> PERMITTED_STATUS = List.of(CANCELED, PENDING);



    public void isEventExist(Set<Long> eventIds) {
        if (eventIds != null && !eventIds.isEmpty() && eventRepository.countEvents(eventIds) != eventIds.size()) {
            String message = String.format("Check the list of event ids=%s, some of the events were not found", eventIds);
            throw new ObjectNotFoundException(message);
        }
    }

    public void isAnyLinkedEventsForCategory(long categoryId) {
        if (eventRepository.existsByCategory(categoryId)) {
            String message = String.format("Category id=%d have connected events", categoryId);
            throw new RulesViolationException(message);
        }
    }

    public void isCorrectDateOrder(LocalDateTime start, LocalDateTime finish) {
        if (start != null && finish != null && finish.isBefore(start)) {
            String message = String.format("The dates violate order: %s must be before %s", start, finish);
            throw new ArgumentNotValidException(message);
        }
    }

    public void isEventHasCorrectStatusToUpdate(EventState state) {
        for (EventState allowed : PERMITTED_STATUS) {
            if (state.equals(allowed)) {
                return;
            }
        }
        String message = String.format("Only %s can be changed", PERMITTED_STATUS);
        throw new RulesViolationException(message);
    }

    public void isDateNotTooEarlyUser(LocalDateTime eventDate) {
        isDateNotTooEarly(eventDate, TIME_GAP_USER);
    }

    public void isDateNotTooEarlyAdmin(LocalDateTime eventDate) {
        isDateNotTooEarly(eventDate, TIME_GAP_ADMIN);
    }

    private void isDateNotTooEarly(LocalDateTime eventDate, Duration gap) {
        LocalDateTime earliestPossibleTime = LocalDateTime.now().plus(gap);
        if (eventDate.isBefore(earliestPossibleTime)) {
            String message = String.format("Start of event must be not earlier than %d hours before now",
                    gap.getSeconds() / 60 / 60);
            throw new ArgumentNotValidException(message);
        }
    }

}
