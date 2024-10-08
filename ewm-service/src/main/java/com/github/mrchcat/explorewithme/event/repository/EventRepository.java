package com.github.mrchcat.explorewithme.event.repository;

import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.event.model.EventState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, EventCustomRepository {

    @Query(value = """
            SELECT e
            FROM Event AS e
            WHERE e.initiator.id=:userId
            """)
    List<Event> getAllByUserId(long userId, Pageable pageable);

    @Query(value = """
            SELECT e
            FROM Event AS e
            WHERE e.id=:eventId AND e.initiator.id=:userId
            """)
    Optional<Event> getByIdByUserId(long userId, long eventId);

    @Query(value = """
            SELECT e
            FROM Event AS e
            WHERE e.id=:eventId AND e.state=:state
            """)
    Optional<Event> getByIdAndStatus(long eventId, EventState state);

    @Query(value = """
            SELECT e
            FROM Event AS e
            WHERE e.id=:eventId AND e.initiator.id=:userId
            """)
    Optional<Event> getByIdAndInitiator(long userId, long eventId);
}
