package com.github.mrchcat.explorewithme.event.repository;

import com.github.mrchcat.explorewithme.event.dto.EventAdminSearchDto;
import com.github.mrchcat.explorewithme.event.dto.EventPublicSearchDto;
import com.github.mrchcat.explorewithme.event.model.Event;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventCustomRepository {

    List<Event> getAllEventByQuery(EventAdminSearchDto queryParams);

    List<Event> getAllEventByQuery(EventPublicSearchDto queryParams);

}
