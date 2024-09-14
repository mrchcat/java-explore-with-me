package com.github.mrchcat.explorewithme.event.repository;

import com.github.mrchcat.explorewithme.event.dto.EventSearchDto;
import com.github.mrchcat.explorewithme.event.model.Event;

import java.util.List;

public interface EventCustomRepository {
    List<Event> getAllEventDtoByQuery(EventSearchDto queryParams);
}
