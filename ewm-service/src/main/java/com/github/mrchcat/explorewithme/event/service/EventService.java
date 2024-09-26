package com.github.mrchcat.explorewithme.event.service;

import com.github.mrchcat.explorewithme.event.dto.EventAdminSearchDto;
import com.github.mrchcat.explorewithme.event.dto.EventAdminUpdateDto;
import com.github.mrchcat.explorewithme.event.dto.EventCreateDto;
import com.github.mrchcat.explorewithme.event.dto.EventDto;
import com.github.mrchcat.explorewithme.event.dto.EventPrivateUpdateDto;
import com.github.mrchcat.explorewithme.event.dto.EventPublicSearchDto;
import com.github.mrchcat.explorewithme.event.dto.EventShortDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventService {

    EventDto create(long userId, EventCreateDto createDto);

    EventDto updateByUser(long userId, long eventId, EventPrivateUpdateDto createDto);

    EventDto updateByAdmin(long eventId, EventAdminUpdateDto updateDtoto);

    List<EventShortDto> getAllShortDtoByUser(long userId, Pageable pageable);

    EventDto getDtoByIdAndUser(long userId, long eventId);

    List<EventDto> getAllByQuery(EventAdminSearchDto query);

    List<EventShortDto> getAllByQuery(EventPublicSearchDto query);

    EventDto getDtoById(long eventId);
}
