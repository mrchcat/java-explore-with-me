package com.github.mrchcat.explorewithme.event.repository;

import com.github.mrchcat.explorewithme.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, EventCustomRepository {

    @Query(value = """
            SELECT *
            FROM events
            WHERE initiator_id=:userId
            LIMIT :size
            OFFSET :from
            """, nativeQuery = true)
    List<Event> getAllEventsByUserId(long userId, long from, long size);

    @Query(value = """
            SELECT e
            FROM Event AS e
            WHERE e.id=:eventId AND e.initiator=:userId
            """)
    Optional<Event> getEventByIdByUserId(long userId, long eventId);

}
