package com.github.mrchcat.explorewithme.mapper;

import com.github.mrchcat.explorewithme.RequestCreateDto;
import com.github.mrchcat.explorewithme.model.Request;

public class RequestMapper {

    public static Request toRequest(RequestCreateDto createDTO) {
        return Request.builder()
                .application(createDTO.getApp())
                .uri(createDTO.getUri().toLowerCase())
                .ip(createDTO.getIp())
                .timestamp(createDTO.getTimestamp())
                .build();
    }
}
