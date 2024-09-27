package com.github.mrchcat.explorewithme.comments.repository;

import com.github.mrchcat.explorewithme.comments.dto.CommentAdminSearchDto;
import com.github.mrchcat.explorewithme.comments.model.Comment;
import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.event.model.EventState;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CommentCustomRepositoryImpl implements CommentCustomRepository {
    @PersistenceContext
    EntityManager em;

    @Override
    public List<Comment> getAllCommentsByQuery(CommentAdminSearchDto qp) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Comment> query = builder.createQuery(Comment.class);
        Root<Comment> root = query.from(Comment.class);

        List<Predicate> wherePredicates = new ArrayList<>();

        List<Long> commentIds = qp.getCommentId();
        if (commentIds != null && !commentIds.isEmpty()) {
            Predicate inCommentList = root.get("id").in(commentIds);
            wherePredicates.add(inCommentList);
        }

        List<Long> eventIds = qp.getEventId();
        if (eventIds != null && !eventIds.isEmpty()) {
            Predicate inEventList = root.get("event").in(eventIds);
            wherePredicates.add(inEventList);
        }

        EventState eventState = qp.getEventState();
        if (eventState != null) {
            Join<Comment, Event> eventJoin = root.join("event");
            Predicate isEventState = builder.equal(eventJoin.get("state"), eventState);
            wherePredicates.add(isEventState);
        }

        List<Long> userIds = qp.getUserId();
        if (userIds != null && !userIds.isEmpty()) {
            Predicate inUserList = root.get("author").in(userIds);
            wherePredicates.add(inUserList);
        }

        Boolean editable = qp.getEditable();
        if (editable != null) {
            Predicate isEditable = builder.equal(root.get("editable"), editable);
            wherePredicates.add(isEditable);
        }

        String text = qp.getText();
        if (text != null && !text.isEmpty()) {
            Predicate textSearch = builder.like(builder.lower(root.get("text")), "%" + text.toLowerCase() + "%");
        }

        var start = qp.getStart();
        if (start != null) {
            Predicate greaterThenOrEqual = builder.greaterThanOrEqualTo(root.get("lastModification"), start);
            wherePredicates.add(greaterThenOrEqual);
        }
        var end = qp.getEnd();
        if (end != null) {
            Predicate lessThenOrEqual = builder.lessThanOrEqualTo(root.get("lastModification"), end);
            wherePredicates.add(lessThenOrEqual);
        }

        query.where(builder.and(wherePredicates.toArray(new Predicate[]{})));

        int pageNumber = qp.getPageable().getPageNumber();
        int pageSize = qp.getPageable().getPageSize();
        var limitedQuery = em.createQuery(query)
                .setFirstResult(pageNumber * pageSize)
                .setMaxResults(pageSize);

        return limitedQuery.getResultList();
    }
}
