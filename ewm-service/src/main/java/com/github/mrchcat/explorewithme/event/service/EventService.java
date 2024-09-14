package com.github.mrchcat.explorewithme.event.service;

import com.github.mrchcat.explorewithme.event.dto.EventCreateDto;
import com.github.mrchcat.explorewithme.event.dto.EventDto;
import com.github.mrchcat.explorewithme.event.dto.EventSearchDto;
import com.github.mrchcat.explorewithme.event.dto.EventShortDto;
import com.github.mrchcat.explorewithme.event.dto.EventUpdateDto;
import com.github.mrchcat.explorewithme.event.model.Event;

import java.util.List;

public interface EventService {

    EventDto create(long userId, EventCreateDto createDto);

    EventDto updateByUser(long userId, long eventId, EventUpdateDto createDto);

    EventDto updateByAdmin(long eventId, EventUpdateDto updateDtoto);

    List<EventShortDto> getAllShortDtoByUser(long userId, long from, long size);

    EventDto getDtoByIdAndUser(long userId, long eventId);

    Event getById(long eventId);

    List<EventDto> getAllByQuery(EventSearchDto query);

}
