package com.github.mrchcat.explorewithme.event.repository;

import com.github.mrchcat.explorewithme.event.dto.EventSearchDto;
import com.github.mrchcat.explorewithme.event.model.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

public class EventCustomRepositoryImpl implements EventCustomRepository {
    @PersistenceContext
    EntityManager em;

    public List<Event> getAllEventDtoByQuery(EventSearchDto qp) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Event> query = builder.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);

        List<Predicate> wherePredicates = new ArrayList<>();
        var userIds = qp.getUserIds();
        if (userIds != null && !userIds.isEmpty()) {
            Predicate inUserList = root.get("initiator").in(userIds);
            wherePredicates.add(inUserList);
        }
        var states = qp.getStates();
        if (states != null && !states.isEmpty()) {
            Predicate inStateList = root.get("state").in(states);
            wherePredicates.add(inStateList);
        }
        var categoryIds = qp.getCategoryIds();
        if (states != null && !states.isEmpty()) {
            Predicate inCategoryList = root.get("id").in(categoryIds);
            wherePredicates.add(inCategoryList);
        }
        var start = qp.getStart();
        if (start != null) {
            Predicate greaterThenOrEqual = builder.greaterThanOrEqualTo(root.get("eventDate"), start);
            wherePredicates.add(greaterThenOrEqual);
        }
        var end = qp.getEnd();
        if (end != null) {
            Predicate lessThenOrEqual = builder.lessThanOrEqualTo(root.get("eventDate"), end);
            wherePredicates.add(lessThenOrEqual);
        }
        query.where(builder.and(wherePredicates.toArray(new Predicate[]{})));
        var limitedQuery = em.createQuery(query)
                .setFirstResult(qp.getFrom())
                .setMaxResults(qp.getSize());

        return limitedQuery.getResultList();
    }
}
