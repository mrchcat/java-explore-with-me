package com.github.mrchcat.explorewithme.request.service;

import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.event.service.EventService;
import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
import com.github.mrchcat.explorewithme.request.dto.RequestDto;
import com.github.mrchcat.explorewithme.request.dto.RequestStatusUpdateDto;
import com.github.mrchcat.explorewithme.request.dto.RequestStatusUpdateResult;
import com.github.mrchcat.explorewithme.request.mapper.RequestMapper;
import com.github.mrchcat.explorewithme.request.model.Request;
import com.github.mrchcat.explorewithme.request.model.RequestStatus;
import com.github.mrchcat.explorewithme.request.model.RequestUpdateStatus;
import com.github.mrchcat.explorewithme.request.repository.RequestRepository;
import com.github.mrchcat.explorewithme.request.validator.RequestValidator;
import com.github.mrchcat.explorewithme.user.model.User;
import com.github.mrchcat.explorewithme.user.service.UserService;
import com.github.mrchcat.explorewithme.event.validator.EventValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.github.mrchcat.explorewithme.request.model.RequestStatus.CANCELED;
import static com.github.mrchcat.explorewithme.request.model.RequestStatus.CONFIRMED;
import static java.lang.Math.min;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final RequestValidator requestValidator;
    private final EventValidator eventValidator;
    private final EventService eventService;
    private final UserService userService;


    @Override
    public RequestDto create(long userId, long eventId) {
        User user = userService.getById(userId);
        Event event = eventService.getById(eventId);
        requestValidator.isRequestExists(userId, eventId);
        requestValidator.isRequestForOwnEvent(userId, event);
        requestValidator.isPublishedEvent(event);
        Request requestToSave = Request.builder()
                .requester(user)
                .event(event)
                .build();
        if (canConfirmAllRequests(event)) {
            requestToSave.setStatus(CONFIRMED);
            eventService.incrementConfirmedRequest(event);
        } else {
            requestToSave.setStatus(RequestStatus.PENDING);
        }
        Request savedRequest = requestRepository.save(requestToSave);
        log.info("User id={} created request {}", userId, savedRequest);
        return RequestMapper.toDto(savedRequest);
    }

    @Override
    public RequestDto cancel(long userId, long requestId) {
        Request request = getByIdByUser(userId, requestId);
        RequestStatus oldStatus = request.getStatus();
        request.setStatus(CANCELED);
        Request savedRequest = requestRepository.save(request);
        if (oldStatus.equals(CONFIRMED)) {
            eventService.decrementConfirmedRequest(request.getEvent());
        }
        log.info("User id={} canceled request {}", userId, savedRequest);
        return RequestMapper.toDto(savedRequest);
    }

    @Override
    public List<RequestDto> getAllDtoByRequester(long userId) {
        List<Request> requests = requestRepository.getByRequester(userId);
        return RequestMapper.toDto(requests);
    }

    @Override
    public List<RequestDto> getAllDtoByInitiatorAndEvent(long userId, long eventId) {
        List<Request> requests = requestRepository.getByInitiatorAndEvent(userId, eventId);
        return RequestMapper.toDto(requests);
    }

    private Request getByIdByUser(long userId, long requestId) {
        return requestRepository.getByIdByRequester(userId, requestId).orElseThrow(() -> {
            String message = String.format("Request with id=%d for user id=%d was not found", requestId, userId);
            return new ObjectNotFoundException(message);
        });
    }

    @Override
    public RequestStatusUpdateResult updateStatus(long userId, long eventId, RequestStatusUpdateDto updates) {
        Event event = eventService.getByIdAndInitiator(userId, eventId);
        int freeLimit;
        if (requestValidator.isNoLimits(event)) {
            freeLimit = Integer.MAX_VALUE;
        } else {
            freeLimit = event.getParticipantLimit() - event.getConfirmedRequests();
            requestValidator.isFreeLimitEnough(event);
        }
        List<Request> requests = requestRepository.findAllById(updates.getRequestIds());
        requestValidator.isRequestCorrespondEvent(event, requests);

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();
        if (updates.getStatus().equals(RequestUpdateStatus.CONFIRMED)) {
            int allowed = min(requests.size(), freeLimit);
            eventService.incrementConfirmedRequest(event, allowed);
            for (int i = 0; i < allowed; i++) {
                Request r = requests.get(i);
                requestValidator.isPending(r);
                confirmedRequests.add(r);
            }
            for (int i = allowed; i < requests.size() - 1; i++) {
                Request r = requests.get(i);
                rejectedRequests.add(r);
            }
        } else {
            for (Request r : requests) {
                requestValidator.isPending(r);
                rejectedRequests.add(r);
            }
        }
        confirmedRequests.forEach(r -> r.setStatus(CONFIRMED));
        rejectedRequests.forEach(r -> r.setStatus(RequestStatus.REJECTED));
        requestRepository.saveAll(confirmedRequests);
        requestRepository.saveAll(rejectedRequests);

        log.info("User id={} updated requests {}", userId, requests);
        return RequestStatusUpdateResult.builder()
                .confirmedRequests(RequestMapper.toDto(confirmedRequests))
                .rejectedRequests(RequestMapper.toDto(rejectedRequests))
                .build();
    }

    private boolean canConfirmAllRequests(Event event) {
        return !event.isRequestModeration() || event.getParticipantLimit() == 0;
    }
}
