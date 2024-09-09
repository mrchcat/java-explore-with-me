package com.github.mrchcat.explorewithme.service;

import com.github.mrchcat.explorewithme.RequestCreateDto;
import com.github.mrchcat.explorewithme.RequestStatisticDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    void addRequest(RequestCreateDto createDto);

    List<RequestStatisticDto> getRequestStatistic(LocalDateTime start,
                                                  LocalDateTime end,
                                                  String[] uris,
                                                  boolean unique);
}
