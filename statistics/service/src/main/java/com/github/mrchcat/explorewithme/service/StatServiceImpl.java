package com.github.mrchcat.explorewithme.service;

import com.github.mrchcat.explorewithme.RequestCreateDTO;
import com.github.mrchcat.explorewithme.RequestStatisticDTO;
import com.github.mrchcat.explorewithme.mapper.RequestMapper;
import com.github.mrchcat.explorewithme.model.Request;
import com.github.mrchcat.explorewithme.repository.StatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {
    public final StatRepository statRepository;

    @Override
    @Transactional
    public void addRequest(RequestCreateDTO createDTO) {
        Request savedRequest = statRepository.save(RequestMapper.toRequest(createDTO));
        log.info("{} saved ", savedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestStatisticDTO> getRequestStatistic(LocalDateTime start,
                                                         LocalDateTime end,
                                                         String[] uris,
                                                         boolean unique) {
        return statRepository.getRequestStatistic(start, end, uris, unique);
    }
}
