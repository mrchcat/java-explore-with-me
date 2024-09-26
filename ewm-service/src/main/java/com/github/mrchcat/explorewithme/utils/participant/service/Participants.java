package com.github.mrchcat.explorewithme.utils.participant.service;

import com.github.mrchcat.explorewithme.event.model.Event;

import java.util.List;
import java.util.Map;

public interface Participants {

    int getEventParticipants(Event event);

    Map<Long, Integer> getEventParticipants(List<Event> events);

}
