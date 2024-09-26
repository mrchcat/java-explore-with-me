package com.github.mrchcat.explorewithme.request.controller;

import com.github.mrchcat.explorewithme.request.dto.RequestDto;
import com.github.mrchcat.explorewithme.request.dto.RequestStatusUpdateDto;
import com.github.mrchcat.explorewithme.request.dto.RequestStatusUpdateResult;
import com.github.mrchcat.explorewithme.request.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class RequestPrivateController {
    private final RequestService requestService;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    RequestDto createRequest(@PathVariable(name = "userId") long userId,
                             @RequestParam(name = "eventId") long eventId) {
        log.info("Private API: received request from user id={} to create event request for event id={}", userId, eventId);
        return requestService.create(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    RequestDto cancelRequest(@PathVariable(name = "userId") long userId,
                             @PathVariable(name = "requestId") long requestId) {
        log.info("Private API: received request from user id={} to cancel event request {}", userId, requestId);
        return requestService.cancel(userId, requestId);
    }

    @GetMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    List<RequestDto> getAllRequestsByRequester(@PathVariable(name = "userId") long userId) {
        log.info("Private API: received request from user id={} to get all his/her requests for another events", userId);
        return requestService.getAllDtoByRequester(userId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    List<RequestDto> getAllRequestsByInitiatorAndEvent(@PathVariable(name = "userId") long userId,
                                                       @PathVariable(name = "eventId") long eventId) {
        log.info("Private API: received request from user id={} to get all requests for event {}", userId, eventId);
        return requestService.getAllDtoByInitiatorAndEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    RequestStatusUpdateResult answerRequest(@PathVariable(name = "userId") long userId,
                                            @PathVariable(name = "eventId") long eventId,
                                            @RequestBody @Valid RequestStatusUpdateDto updates) {
        log.info("Received request from user id={} for event={} to update as {}", userId, eventId, updates);
        return requestService.updateStatus(userId, eventId, updates);
    }
}
