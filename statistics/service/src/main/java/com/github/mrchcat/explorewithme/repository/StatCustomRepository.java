package com.github.mrchcat.explorewithme.repository;

import com.github.mrchcat.explorewithme.RequestStatisticDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface StatCustomRepository {
    List<RequestStatisticDTO> getRequestStatistic(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique);
}
