package com.github.mrchcat.explorewithme.request.service;

import com.github.mrchcat.explorewithme.request.dto.RequestDto;
import com.github.mrchcat.explorewithme.request.dto.RequestStatusUpdateDto;
import com.github.mrchcat.explorewithme.request.dto.RequestStatusUpdateResult;

import java.util.List;

public interface RequestService {
    RequestDto create(long userId, long eventId);

    RequestDto cancel(long userId, long requestId);

    List<RequestDto> getAllDtoByRequester(long userId);

    List<RequestDto> getAllDtoByInitiatorAndEvent(long userId, long eventId);

    RequestStatusUpdateResult updateStatus(long userId, long eventId, RequestStatusUpdateDto updates);
}
