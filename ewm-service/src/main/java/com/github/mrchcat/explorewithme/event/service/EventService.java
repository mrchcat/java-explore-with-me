package com.github.mrchcat.explorewithme.event.service;

import com.github.mrchcat.explorewithme.event.dto.EventCreateDto;
import com.github.mrchcat.explorewithme.event.dto.EventDto;
import com.github.mrchcat.explorewithme.event.dto.EventShortDto;
import com.github.mrchcat.explorewithme.event.dto.EventUpdateDto;
import com.github.mrchcat.explorewithme.event.model.Event;

import java.util.List;

public interface EventService {

    EventDto createEvent(long userId, EventCreateDto createDto);

    EventDto updateEventByUser(long userId, long eventId, EventUpdateDto createDto);

    EventDto updateEventByAdmin(long eventId, EventUpdateDto updateDtoto);

    List<EventShortDto> getAllShortEventDtoByUser(long userId, long from, long size);

    EventDto getEventDtoByIdByUser(long userId, long eventId);

    Event getEventById(long eventId);
}
