package com.github.mrchcat.explorewithme.request.repository;

import com.github.mrchcat.explorewithme.request.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("""
            SELECT CASE WHEN COUNT(r)>0 THEN TRUE ELSE FALSE END
            FROM Request AS r
            WHERE r.requester.id=:userId AND r.event.id=:eventId
            """)
    boolean exists(long userId, long eventId);

    @Query("""
            SELECT r
            FROM Request AS r
            WHERE r.id=:requestId AND r.requester.id=:userId
            """)
    Optional<Request> getByIdByRequester(long userId, long requestId);


    @Query("""
            SELECT r
            FROM Request AS r
            WHERE r.requester.id=:userId
            """)
    List<Request> getByRequester(long userId);

    @Query("""
            SELECT r
            FROM Request AS r
            JOIN r.event AS e
            WHERE e.initiator.id=:userId AND e.id=:eventId
            """)
    List<Request> getByInitiatorAndEvent(long userId, long eventId);

}
