package com.github.mrchcat.explorewithme.event.service;

import com.github.mrchcat.explorewithme.event.dto.EventAdminSearchDto;
import com.github.mrchcat.explorewithme.event.dto.EventCreateDto;
import com.github.mrchcat.explorewithme.event.dto.EventDto;
import com.github.mrchcat.explorewithme.event.dto.EventPublicSearchDto;
import com.github.mrchcat.explorewithme.event.dto.EventShortDto;
import com.github.mrchcat.explorewithme.event.dto.EventUpdateDto;
import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.event.model.EventSortAttribute;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventService {

    EventDto create(long userId, EventCreateDto createDto);

    EventDto updateByUser(long userId, long eventId, EventUpdateDto createDto);

    EventDto updateByAdmin(long eventId, EventUpdateDto updateDtoto);

    List<EventShortDto> getAllShortDtoByUser(long userId, Pageable pageable);

    EventDto getDtoByIdAndUser(long userId, long eventId);

    Event getById(long eventId);

    List<EventDto> getAllByQuery(EventAdminSearchDto query, Pageable pageable);

    List<EventShortDto> getAllByQuery(EventPublicSearchDto query,
                                      Pageable pageable,
                                      EventSortAttribute sort,
                                      HttpServletRequest request);

    EventShortDto getShortDtoById(long eventId,HttpServletRequest request);

}
