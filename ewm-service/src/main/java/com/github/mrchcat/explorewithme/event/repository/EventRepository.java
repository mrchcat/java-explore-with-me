package com.github.mrchcat.explorewithme.event.repository;

import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.user.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, EventCustomRepository {

    @Query(value = """
            SELECT e
            FROM Event AS e
            WHERE e.initiator=:userId
            LIMIT :size
            OFFSET :from
            """, nativeQuery = true)
    @EntityGraph(attributePaths = {"user", "collection", "event"})
    List<Event> getAllEventsByUserId(long userId, long from, long size);

    @Query(value = """
            SELECT e
            FROM Event AS e
            WHERE e.id=:eventId AND e.initiator=:userId
            """)
    @EntityGraph(attributePaths = {"user", "collection", "event"})
    Optional<Event> getEventByIdByUserId(long userId, long eventId);

}
