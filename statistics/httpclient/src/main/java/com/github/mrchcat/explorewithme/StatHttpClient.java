package com.github.mrchcat.explorewithme;

import java.io.IOException;
import java.util.List;

public interface StatHttpClient {

    void addRequest(RequestCreateDto createDTO);

    List<RequestStatisticDto> getRequestStatistic(RequestQueryParamDto qp) throws IOException;
}
