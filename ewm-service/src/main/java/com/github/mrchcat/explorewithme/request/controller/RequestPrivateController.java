package com.github.mrchcat.explorewithme.request.controller;

import com.github.mrchcat.explorewithme.request.dto.RequestDto;
import com.github.mrchcat.explorewithme.request.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    List<RequestDto> getAllRequests(@PathVariable(name = "userId") long userId) {
        log.info("Private API: received request from user id={} to get all his event requests");
        return requestService.getAllDtoByUser(userId);
    }

}
