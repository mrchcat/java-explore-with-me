package com.github.mrchcat.explorewithme.repository;

import com.github.mrchcat.explorewithme.RequestQueryParamDto;
import com.github.mrchcat.explorewithme.RequestStatisticDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatCustomRepository {

    List<RequestStatisticDto> getRequestStatistic(RequestQueryParamDto queryParams);

}
