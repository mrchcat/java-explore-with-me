package com.github.mrchcat.explorewithme.event.controller;

import com.github.mrchcat.explorewithme.event.dto.EventCreateDto;
import com.github.mrchcat.explorewithme.event.dto.EventDto;
import com.github.mrchcat.explorewithme.event.dto.EventShortDto;
import com.github.mrchcat.explorewithme.event.dto.EventUpdateDto;
import com.github.mrchcat.explorewithme.event.service.EventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    EventDto createEvent(@PathVariable(name = "userId") long userId,
                         @RequestBody @Valid EventCreateDto createDto) {
        log.info("Private API: received request from user id={} to create {}", userId, createDto);
        return eventService.create(userId, createDto);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    EventDto updateEvent(@PathVariable(name = "userId") long userId,
                         @PathVariable(name = "eventId") long eventId,
                         @RequestBody @Valid EventUpdateDto updateDto) {
        log.info("Private API: received request from user id={} to update {}", userId, updateDto);
        return eventService.updateByUser(userId, eventId, updateDto);
    }

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    List<EventShortDto> getAllEventsByUser(
            @PathVariable(name = "userId") long userId,
            @RequestParam(name = "from", defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10", required = false) @PositiveOrZero Integer size) {
        log.info("Private API: received request from user id={} to get all his events with parameters from={} size={}",
                userId, from, size);
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        return eventService.getAllShortDtoByUser(userId, pageable);
    }

    @GetMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    EventDto getEventByIdByUser(@PathVariable(name = "userId") long userId,
                                @PathVariable(name = "eventId") long eventId) {
        log.info("Private API: received request from user id={} to get event with id={}", userId, eventId);
        return eventService.getDtoByIdAndUser(userId, eventId);
    }

}
