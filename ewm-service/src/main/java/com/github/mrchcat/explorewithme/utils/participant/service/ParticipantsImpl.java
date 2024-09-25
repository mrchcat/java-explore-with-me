package com.github.mrchcat.explorewithme.utils.participant.service;

import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.utils.participant.model.EventParticipant;
import com.github.mrchcat.explorewithme.utils.participant.repository.ParticipantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ParticipantsImpl implements Participants {
    private final ParticipantRepository participantRepository;

    @Override
    public int getEventParticipants(Event event) {
        return participantRepository.getEventParticipants(event.getId());
    }

    @Override
    public Map<Long, Integer> getEventParticipants(List<Event> events) {
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();
        List<EventParticipant> eventParticipantList = participantRepository.getEventParticipants(eventIds);
        Map<Long, Integer> eventParticipantMap = eventParticipantList.stream()
                .collect(Collectors.toMap(EventParticipant::getId, EventParticipant::getParticipants));
        return eventIds.stream()
                .collect(Collectors.toMap(Function.identity(), id -> eventParticipantMap.getOrDefault(id, 0)));
    }
}
