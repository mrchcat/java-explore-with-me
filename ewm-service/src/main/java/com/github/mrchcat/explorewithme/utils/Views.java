package com.github.mrchcat.explorewithme.utils;

import com.github.mrchcat.explorewithme.event.model.Event;

import java.util.List;
import java.util.Map;

public interface Views {

    long getEventViews(Event event);

    Map<Long, Long> getEventViews(List<Event> events);
}
