package com.github.mrchcat.explorewithme.service;

import com.github.mrchcat.explorewithme.RequestCreateDTO;
import com.github.mrchcat.explorewithme.RequestStatisticDTO;
import com.github.mrchcat.explorewithme.mapper.RequestMapper;
import com.github.mrchcat.explorewithme.repository.StatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    public final StatRepository statRepository;

    @Override
    public void addRequest(RequestCreateDTO createDTO) {
        statRepository.save(RequestMapper.toRequest(createDTO));
    }

    @Override
    public List<RequestStatisticDTO> getRequestStatistic(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        return statRepository.getRequestStatistic(start, end, uris, unique);
    }
}
