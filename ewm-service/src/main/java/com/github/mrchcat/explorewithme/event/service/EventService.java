package com.github.mrchcat.explorewithme.event.service;

import com.github.mrchcat.explorewithme.event.dto.EventAdminSearchDto;
import com.github.mrchcat.explorewithme.event.dto.EventAdminUpdateDto;
import com.github.mrchcat.explorewithme.event.dto.EventCreateDto;
import com.github.mrchcat.explorewithme.event.dto.EventDto;
import com.github.mrchcat.explorewithme.event.dto.EventPrivateUpdateDto;
import com.github.mrchcat.explorewithme.event.dto.EventPublicSearchDto;
import com.github.mrchcat.explorewithme.event.dto.EventShortDto;
import com.github.mrchcat.explorewithme.event.model.Event;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface EventService {

    EventDto create(long userId, EventCreateDto createDto);

    EventDto updateByUser(long userId, long eventId, EventPrivateUpdateDto createDto);

    EventDto updateByAdmin(long eventId, EventAdminUpdateDto updateDtoto);

    List<EventShortDto> getAllShortDtoByUser(long userId, Pageable pageable);

    EventDto getDtoByIdAndUser(long userId, long eventId);

    Event getById(long eventId);

    Event getByIdAndInitiator(long userId, long eventId);

    List<Event> getById(List<Long> eventIds);

    List<EventDto> getAllByQuery(EventAdminSearchDto query);

    List<EventShortDto> getAllByQuery(EventPublicSearchDto query);

    EventDto getDtoById(long eventId);

    void decrementConfirmedRequest(Event event);

    void incrementConfirmedRequest(Event event, int number);

    void incrementConfirmedRequest(Event event);

    long getEventViews(Event event);

    Map<Long, Long> getEventViews(List<Event> events);
}
