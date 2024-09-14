package com.github.mrchcat.explorewithme.event.controller;

import com.github.mrchcat.explorewithme.event.dto.EventDto;
import com.github.mrchcat.explorewithme.event.dto.EventUpdateDto;
import com.github.mrchcat.explorewithme.event.service.EventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
public class EventAdminController {
    private final EventService eventService;

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    EventDto updateEvent(@PathVariable(name = "eventId", required = true) long eventId,
                         @RequestBody @Valid EventUpdateDto updateDto) {
        log.info("Admin API: received request to update event id={}", eventId, updateDto);
        return eventService .updateEventByAdmin(eventId,updateDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<EventDto> getAllEvents(@RequestParam(name = "users", required = false) List<Long> userIds,
                                ){


    }



}
