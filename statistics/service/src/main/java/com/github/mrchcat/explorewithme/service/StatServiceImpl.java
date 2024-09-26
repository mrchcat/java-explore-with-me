package com.github.mrchcat.explorewithme.service;

import com.github.mrchcat.explorewithme.RequestCreateDto;
import com.github.mrchcat.explorewithme.RequestQueryParamDto;
import com.github.mrchcat.explorewithme.RequestStatisticDto;
import com.github.mrchcat.explorewithme.mapper.RequestMapper;
import com.github.mrchcat.explorewithme.model.Request;
import com.github.mrchcat.explorewithme.repository.StatRepository;
import com.github.mrchcat.explorewithme.validator.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {
    public final StatRepository statRepository;

    @Override
    @Transactional
    public void addRequest(RequestCreateDto createDto) {
        Request savedRequest = statRepository.save(RequestMapper.toRequest(createDto));
        log.info("{} saved ", savedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestStatisticDto> getRequestStatistic(RequestQueryParamDto queryParams) {
        Validator.validate(queryParams);
        List<RequestStatisticDto> statisticDtos = statRepository.getRequestStatistic(queryParams);
        log.info("{}", statisticDtos);
        return statisticDtos;
    }
}
