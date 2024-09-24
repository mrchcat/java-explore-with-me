package com.github.mrchcat.explorewithme.event.controller;

import com.github.mrchcat.explorewithme.event.dto.EventAdminSearchDto;
import com.github.mrchcat.explorewithme.event.dto.EventAdminUpdateDto;
import com.github.mrchcat.explorewithme.event.dto.EventDto;
import com.github.mrchcat.explorewithme.event.model.EventState;
import com.github.mrchcat.explorewithme.event.service.EventService;
import com.github.mrchcat.explorewithme.exception.ArgumentNotValidException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
public class EventAdminController {
    private final EventService eventService;

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    EventDto updateEvent(@PathVariable(name = "eventId") long eventId,
                         @RequestBody @Valid EventAdminUpdateDto updateDto) {
        log.info("Admin API: received request to update event id={} with {}", eventId, updateDto);
        return eventService.updateByAdmin(eventId, updateDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<EventDto> getAllEvents(@RequestParam(name = "users", required = false) List<Long> userIds,
                                @RequestParam(name = "states", required = false) List<EventState> states,
                                @RequestParam(name = "categories", required = false) List<Long> categoryIds,
                                @RequestParam(name = "rangeStart", required = false)
                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                @RequestParam(name = "rangeEnd", required = false)
                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                @RequestParam(name = "size", defaultValue = "10", required = false) @Positive Integer size) {

        isCorrectDateOrder(start, end);
        EventAdminSearchDto searchDto = EventAdminSearchDto.builder()
                .userIds(userIds)
                .states(states)
                .categoryIds(categoryIds)
                .start(start)
                .end(end)
                .pageable(PageRequest.of(from > 0 ? from / size : 0, size))
                .build();
        return eventService.getAllByQuery(searchDto);
    }

    private void isCorrectDateOrder(LocalDateTime start, LocalDateTime finish) {
        if (start != null && finish != null && finish.isBefore(start)) {
            String message = "The dates violate order: " + start + " must be before " + finish;
            throw new ArgumentNotValidException(message);
        }
    }
}

