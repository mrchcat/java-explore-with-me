package com.github.mrchcat.explorewithme.event.controller;

import com.github.mrchcat.explorewithme.RequestCreateDto;
import com.github.mrchcat.explorewithme.StatHttpClient;
import com.github.mrchcat.explorewithme.event.dto.EventDto;
import com.github.mrchcat.explorewithme.event.dto.EventPublicSearchDto;
import com.github.mrchcat.explorewithme.event.dto.EventShortDto;
import com.github.mrchcat.explorewithme.event.model.EventSortAttribute;
import com.github.mrchcat.explorewithme.event.service.EventService;
import com.github.mrchcat.explorewithme.exception.ArgumentNotValidException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Getter
@Slf4j
public class EventPublicController {
    private final EventService eventService;
    @Value("${app.name}")
    private String appName;
    private final StatHttpClient statHttpClient;

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
        isCorrectDateOrder(start, end);
        EventPublicSearchDto query = EventPublicSearchDto.builder()
                .text(text)
                .categoryIds(categoryIds)
                .paid(paid)
                .end(end)
                .onlyAvailable(onlyAvailable)
                .pageable(PageRequest.of(from > 0 ? from / size : 0, size))
                .eventSortAttribute(sort)
                .build();
        log.info("Public API: received request from {} to get all events with parameters {}",
                request.getRemoteAddr(), query);
        List<EventShortDto> result = eventService.getAllByQuery(query);
        sendToStatService(request);
        return result;
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    EventDto getEventById(HttpServletRequest request,
                          @PathVariable(name = "eventId") long eventId) {
        log.info("PublicAPI: received request to get event id={}", eventId);
        EventDto result = eventService.getDtoById(eventId);
        sendToStatService(request);
        return result;
    }

    private void sendToStatService(HttpServletRequest request) {
        String remoteAddress = request.getRemoteAddr();
        InetAddress ip = null;
        try {
            ip = InetAddress.getByName(remoteAddress);
        } catch (UnknownHostException e) {
            log.error("RemoteAddress {} can not be converted to InetAdress", remoteAddress);
        }
        RequestCreateDto statRequest = RequestCreateDto.builder()
                .app(appName)
                .uri(request.getRequestURI())
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
        log.info("создали запрос {}", statRequest);
        statHttpClient.addRequest(statRequest);
    }

    private void isCorrectDateOrder(LocalDateTime start, LocalDateTime finish) {
        if (start != null && finish != null && finish.isBefore(start)) {
            String message = String.format("The dates violate order: %s must be before %s", start, finish);
            throw new ArgumentNotValidException(message);
        }
    }
}
