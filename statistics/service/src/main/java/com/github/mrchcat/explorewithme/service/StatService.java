package com.github.mrchcat.explorewithme.service;

import com.github.mrchcat.explorewithme.RequestCreateDTO;
import com.github.mrchcat.explorewithme.RequestStatisticDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    void addRequest(RequestCreateDTO createDTO);

    List<RequestStatisticDTO> getRequestStatistic(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique);

}
