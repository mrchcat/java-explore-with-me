package com.github.mrchcat.explorewithme.utils.participant.repository;

import com.github.mrchcat.explorewithme.utils.participant.model.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<EventParticipant,Long> {

    @Query(value = """
            SELECT e.id, COUNT(*) AS participants
            FROM events AS e
            JOIN requests AS r ON r.event_id=e.id
            WHERE e.id IN (:eventIds) AND r.status='CONFIRMED'
            GROUP BY e.id
            """, nativeQuery = true)
    List<EventParticipant> getEventParticipants(List<Long> eventIds);

    @Query("""
            SELECT COUNT(r)
            FROM Request AS r
            JOIN r.event AS e
            WHERE e.id=:eventId AND r.status='CONFIRMED'
            """)
    int getEventParticipants(long eventId);
}
