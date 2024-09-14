package com.github.mrchcat.explorewithme.validator;

import com.github.mrchcat.explorewithme.category.repository.CategoryRepository;
import com.github.mrchcat.explorewithme.event.model.EventState;
import com.github.mrchcat.explorewithme.event.repository.EventRepository;
import com.github.mrchcat.explorewithme.exception.DataIntegrityException;
import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
import com.github.mrchcat.explorewithme.exception.RulesViolationException;
import com.github.mrchcat.explorewithme.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static com.github.mrchcat.explorewithme.event.model.EventState.CANCELED;
import static com.github.mrchcat.explorewithme.event.model.EventState.PENDING;

@Component
@AllArgsConstructor
public class Validator {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private static final Duration TIME_GAP_USER = Duration.ofHours(2);
    private static final Duration TIME_GAP_ADMIN = Duration.ofHours(1);
    private static final List<EventState> PERMITTED_STATUS = {CANCELED, PENDING};


    public void isEventHasCorrectStatusToUpdate(EventState state) {
        for (EventState allowed : PERMITTED_STATUS) {
            if (state.equals(allowed)) {
                return;
            }
        }
        String message = String.format("Only %s can be changed", PERMITTED_STATUS);
        throw new RulesViolationException(message);
    }

    public void isDateNotTooEarlyUser(LocalDateTime eventDate){
        isDateNotTooEarly(eventDate,TIME_GAP_USER);
    }

    public void isDateNotTooEarlyAdmin(LocalDateTime eventDate){
        isDateNotTooEarly(eventDate,TIME_GAP_ADMIN);
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
