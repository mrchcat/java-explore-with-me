package com.github.mrchcat.explorewithme.event.repository;

import com.github.mrchcat.explorewithme.category.model.Category;
import com.github.mrchcat.explorewithme.event.dto.EventAdminSearchDto;
import com.github.mrchcat.explorewithme.event.dto.EventPublicSearchDto;
import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.event.model.EventState;
import com.github.mrchcat.explorewithme.user.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class EventCustomRepositoryImpl implements EventCustomRepository {
    @PersistenceContext
    EntityManager em;

    public List<Event> getAllEventByQuery(EventAdminSearchDto qp, Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Event> query = builder.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);

        List<Predicate> wherePredicates = new ArrayList<>();
        var userIds = qp.getUserIds();
        if (userIds != null && !userIds.isEmpty()) {
            Join<Event, User> userJoin = root.join("initiator");
            Predicate inUserList = userJoin.get("id").in(userIds);
            wherePredicates.add(inUserList);
        }
        var states = qp.getStates();
        if (states != null && !states.isEmpty()) {
            Predicate inStateList = root.get("state").in(states);
            wherePredicates.add(inStateList);
        }
        var categoryIds = qp.getCategoryIds();
        if (categoryIds != null && !categoryIds.isEmpty()) {
            Join<Event, Category> categoryJoin = root.join("category");
            Predicate inCategoryList = categoryJoin.get("id").in(categoryIds);
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

        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        var limitedQuery = em.createQuery(query)
                .setFirstResult(pageNumber * pageSize)
                .setMaxResults(pageSize);

        return limitedQuery.getResultList();
    }

    @Override
    public List<Event> getAllEventByQuery(EventPublicSearchDto qp, Pageable pageable) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Event> query = builder.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);

        List<Predicate> wherePredicates = new ArrayList<>();
        String text = qp.getText();
        if (text != null && !text.isEmpty()) {
            Predicate textSearchInAnnotation = builder.like(builder.lower(root.get("annotation")),
                    "%" + text.toLowerCase() + "%");
            Predicate textSearchDescription = builder.like(builder.lower(root.get("description")),
                    "%" + text.toLowerCase() + "%");
            Predicate textSearch = builder.or(textSearchInAnnotation, textSearchDescription);
            wherePredicates.add(textSearch);
        }

        var categoryIds = qp.getCategoryIds();
        if (categoryIds != null) {
            Join<Event, Category> categoryJoin = root.join("category");
            Predicate inCategoryList = categoryJoin.get("id").in(categoryIds);
            wherePredicates.add(inCategoryList);
        }

        Boolean paid = qp.getPaid();
        if (paid != null) {
            Predicate isPaid = builder.equal(root.get("paid"), paid);
            wherePredicates.add(isPaid);
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

        Boolean onlyAvailable = qp.getOnlyAvailable();
        if (onlyAvailable != null) {
            Predicate isPaid = builder.greaterThan(root.get("participantLimit"), 0);
            wherePredicates.add(isPaid);
        }

        List<EventState> states = qp.getStates();
        if (states != null && !states.isEmpty()) {
            Predicate inStatusList = root.get("state").in(states);
            wherePredicates.add(inStatusList);
        }

        query.where(builder.and(wherePredicates.toArray(new Predicate[]{})));

        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        var limitedQuery = em.createQuery(query)
                .setFirstResult(pageNumber * pageSize)
                .setMaxResults(pageSize);
        return limitedQuery.getResultList();
    }
}
