package com.github.mrchcat.explorewithme.event.controller;

import com.github.mrchcat.explorewithme.event.dto.EventPublicSearchDto;
import com.github.mrchcat.explorewithme.event.dto.EventShortDto;
import com.github.mrchcat.explorewithme.event.model.EventSortAttribute;
import com.github.mrchcat.explorewithme.event.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Getter
@Slf4j
public class EventPublicController {
    private final EventService eventService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    List<EventShortDto> getAllEvents(HttpServletRequest request,
                                     @RequestParam(name = "text", required = false) String text,
                                     @RequestParam(name = "categories", required = false) List<Long> categoryIds,
                                     @RequestParam(name = "paid", required = false) Boolean paid,
                                     @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                     @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                     @RequestParam(name = "onlyAvailable", required = false) Boolean onlyAvailable,
                                     @RequestParam(name = "sort", required = false) EventSortAttribute sort,
                                     @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                     @RequestParam(name = "size", defaultValue = "10", required = false) @Positive Integer size) {
        EventPublicSearchDto query = EventPublicSearchDto.builder()
                .text(text)
                .categoryIds(categoryIds)
                .paid(paid)
                .end(end)
                .onlyAvailable(onlyAvailable)
                .build();
        if (start != null) {
            query.setStart(start);
        }
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        log.info("Public API: received request from {} to get all events with parameters {} and pagination {}",
                request.getRemoteAddr(), query, pageable);
        return eventService.getAllByQuery(query, pageable, sort, request);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    EventShortDto getEventById(HttpServletRequest request,
                               @PathVariable(name = "eventId") long eventId) {
        log.info("PublicAPI: received request to get event id={}", eventId);
        return eventService.getShortDtoById(eventId, request);
    }

}
