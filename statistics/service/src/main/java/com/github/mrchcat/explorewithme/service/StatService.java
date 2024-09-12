package com.github.mrchcat.explorewithme.service;

import com.github.mrchcat.explorewithme.RequestCreateDto;
import com.github.mrchcat.explorewithme.RequestQueryParamDto;
import com.github.mrchcat.explorewithme.RequestStatisticDto;

import java.util.List;

public interface StatService {

    void addRequest(RequestCreateDto createDto);

    List<RequestStatisticDto> getRequestStatistic(RequestQueryParamDto queryParams);
}
