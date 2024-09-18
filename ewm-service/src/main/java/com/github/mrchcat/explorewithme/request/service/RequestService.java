package com.github.mrchcat.explorewithme.request.service;

import com.github.mrchcat.explorewithme.request.dto.RequestDto;
import com.github.mrchcat.explorewithme.request.model.Request;

import java.util.List;

public interface RequestService {
    RequestDto create(long userId, long eventId);

    RequestDto cancel(long userId, long requestId);

    List<RequestDto> getAllDtoByUser(long userId);

}
