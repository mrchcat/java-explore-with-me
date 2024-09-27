package com.github.mrchcat.explorewithme.comments.repository;

import com.github.mrchcat.explorewithme.comments.dto.CommentAdminSearchDto;
import com.github.mrchcat.explorewithme.comments.model.Comment;
import com.github.mrchcat.explorewithme.comments.model.CommentState;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Sort;
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

        CommentState commentState = qp.getCommentState();
        if (commentState != null) {
            Predicate isCommentState = builder.equal(root.get("state"), commentState);
            wherePredicates.add(isCommentState);
        }

        List<Long> eventIds = qp.getEventId();
        root.fetch("event");
        if (eventIds != null && !eventIds.isEmpty()) {
            Predicate inEventList = root.get("event").get("id").in(eventIds);
            wherePredicates.add(inEventList);
        }

        List<Long> userIds = qp.getUserId();
        root.fetch("author");
        if (userIds != null && !userIds.isEmpty()) {
            Predicate inUserList = root.get("author").get("id").in(userIds);
            wherePredicates.add(inUserList);
        }

        Boolean editable = qp.getEditable();
        if (editable != null) {
            Predicate isEditable = builder.equal(root.get("editable"), editable);
            wherePredicates.add(isEditable);
        }

        String text = qp.getText();
        if (text != null && !text.isEmpty()) {
            text = text.trim().toLowerCase();
            Predicate textSearch = builder.like(builder.lower(root.get("text")), "%" + text + "%");
            wherePredicates.add(textSearch);
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

        List<Sort.Order> order = qp.getPageable().getSort().get().toList();
        String property = order.getFirst().getProperty();
        Sort.Direction direction = order.getFirst().getDirection();
        switch (direction) {
            case ASC -> query.orderBy(builder.asc(root.get(property)));
            case DESC -> query.orderBy(builder.desc(root.get(property)));
        }

        int pageNumber = qp.getPageable().getPageNumber();
        int pageSize = qp.getPageable().getPageSize();
        var limitedQuery = em.createQuery(query)
                .setFirstResult(pageNumber * pageSize)
                .setMaxResults(pageSize);

        return limitedQuery.getResultList();
    }
}
