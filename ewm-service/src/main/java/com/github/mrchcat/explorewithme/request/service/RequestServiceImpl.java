package com.github.mrchcat.explorewithme.request.service;

import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.event.service.EventService;
import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
import com.github.mrchcat.explorewithme.request.dto.RequestDto;
import com.github.mrchcat.explorewithme.request.mapper.RequestMapper;
import com.github.mrchcat.explorewithme.request.model.Request;
import com.github.mrchcat.explorewithme.request.model.RequestStatus;
import com.github.mrchcat.explorewithme.request.repository.RequestRepository;
import com.github.mrchcat.explorewithme.request.validator.RequestValidator;
import com.github.mrchcat.explorewithme.user.model.User;
import com.github.mrchcat.explorewithme.user.service.UserService;
import com.github.mrchcat.explorewithme.validator.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.Boolean.TRUE;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final RequestValidator requestValidator;
    private final Validator validator;
    private final EventService eventService;
    private final UserService userService;


    @Override
    public RequestDto create(long userId, long eventId) {
        User user = userService.getById(userId);
        Event event = eventService.getById(eventId);
        requestValidator.isRequestExists(userId, eventId);
        requestValidator.isRequestForOwnEvent(userId, event);
        requestValidator.isPublishedEvent(event);
        requestValidator.isParticipantLimitExceeded(event);

        Request requestToSave = Request.builder()
                .requester(user)
                .event(event)
                .build();
        if (!event.getRequestModeration()) {
            requestToSave.setStatus(RequestStatus.CONFIRMED);
            eventService.decrementParticipantLimit(event);
        } else {
            requestToSave.setStatus(RequestStatus.PENDING);
        }
        Request savedRequest = requestRepository.save(requestToSave);
        return RequestMapper.toDto(savedRequest);
    }

    @Override
    public RequestDto cancel(long userId, long requestId) {
        Request request = getByIdByUser(userId, requestId);
        RequestStatus oldStatus=request.getStatus();
        request.setStatus(RequestStatus.CANCELED);
        Request savedRequest = requestRepository.save(request);
        if(oldStatus.equals(RequestStatus.CONFIRMED)){
            eventService.incrementParticipantLimit(request.getEvent());
        }
        return RequestMapper.toDto(savedRequest);
    }

    @Override
    public List<RequestDto> getAllDtoByUser(long userId) {
        List<Request> requests = requestRepository.getByRequester(userId);
        return RequestMapper.toDto(requests);
    }

    private Request getByIdByUser(long userId, long requestId) {
        return requestRepository.getByIdByRequester(userId, requestId).orElseThrow(() -> {
            String message = String.format("Request with id=%d for user id=%d was not found", requestId, userId);
            return new ObjectNotFoundException(message);
        });
    }
}
