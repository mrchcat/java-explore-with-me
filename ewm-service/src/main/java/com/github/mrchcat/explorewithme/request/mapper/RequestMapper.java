package com.github.mrchcat.explorewithme.request.mapper;

import com.github.mrchcat.explorewithme.request.dto.RequestDto;
import com.github.mrchcat.explorewithme.request.model.Request;

import java.util.List;

public class RequestMapper {

    public static RequestDto toDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .created(request.getCreated())
                .status(request.getStatus())
                .build();
    }

    public static List<RequestDto> toDto(List<Request> requests) {
        return requests.stream()
                .map(RequestMapper::toDto)
                .toList();
    }


}
