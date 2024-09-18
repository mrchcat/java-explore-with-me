package com.github.mrchcat.explorewithme.validator;

import com.github.mrchcat.explorewithme.category.repository.CategoryRepository;
import com.github.mrchcat.explorewithme.compilation.repository.CompilationRepository;
import com.github.mrchcat.explorewithme.event.model.EventState;
import com.github.mrchcat.explorewithme.event.repository.EventRepository;
import com.github.mrchcat.explorewithme.exception.DataIntegrityException;
import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
import com.github.mrchcat.explorewithme.exception.RulesViolationException;
import com.github.mrchcat.explorewithme.request.repository.RequestRepository;
import com.github.mrchcat.explorewithme.user.repository.UserRepository;
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
public class Validator {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private static final Duration TIME_GAP_USER = Duration.ofHours(2);
    private static final Duration TIME_GAP_ADMIN = Duration.ofHours(1);
    private static final List<EventState> PERMITTED_STATUS = List.of(CANCELED, PENDING);


    public void isCompilationExist(long compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            String message = String.format("Compilation with id=%d was not found", compilationId);
            throw new ObjectNotFoundException(message);
        }
    }


    public void isEventExist(Set<Long> eventIds) {
        if (eventIds != null && !eventIds.isEmpty() && eventRepository.countEvents(eventIds) != eventIds.size()) {
            String message = String.format("Check the list of event ids=%s, some of the events were not found", eventIds);
            throw new ObjectNotFoundException(message);
        }
    }


    public void isEventExist(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            String message = String.format("Event with id=%d was not found", eventId);
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
            throw new RulesViolationException(message);
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
            throw new RulesViolationException(message);
        }
    }

    public void isUserEmailUnique(String userEmail) {
        if (userRepository.existsByEmail(userEmail)) {
            String message = String.format("Email=[%s] is not unique for user", userEmail);
            throw new DataIntegrityException(message);
        }
    }

    public void isUserIdExists(long userId) {
        if (!userRepository.existsById(userId)) {
            String message = String.format("User with id=%d was not found", userId);
            throw new ObjectNotFoundException(message);
        }
    }

    public void isCategoryNameUnique(String categoryName) {
        if (categoryRepository.existsByName(categoryName)) {
            String message = String.format("Name=[%s] is not unique for category", categoryName);
            throw new DataIntegrityException(message);
        }
    }

    public void isCategoryIdExists(long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            String message = String.format("Category with id=%d was not found", categoryId);
            throw new ObjectNotFoundException(message);
        }
    }

    public void isCategoryNameUniqueExclId(long categoryId, String categoryName) {
        if (categoryRepository.existsByNameExclId(categoryId, categoryName)) {
            String message = String.format("%s is not unique for category", categoryName);
            throw new ObjectNotFoundException(message);
        }
    }
}
