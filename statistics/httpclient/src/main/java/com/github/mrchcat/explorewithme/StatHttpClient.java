package com.github.mrchcat.explorewithme;

import org.springframework.http.ResponseEntity;

import java.io.IOError;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface StatHttpClient {

    public void addRequest(RequestCreateDto createDTO);

    public List<RequestStatisticDto> getRequestStatistic(RequestQueryParamDto qp) throws IOException;
}
