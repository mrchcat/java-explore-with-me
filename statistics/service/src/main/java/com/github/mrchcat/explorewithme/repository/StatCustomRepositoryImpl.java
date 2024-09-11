package com.github.mrchcat.explorewithme.repository;

import com.github.mrchcat.explorewithme.RequestQueryParamDto;
import com.github.mrchcat.explorewithme.RequestStatisticDto;
import com.github.mrchcat.explorewithme.model.Request;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Repository
public class StatCustomRepositoryImpl implements StatCustomRepository {
    @PersistenceContext
    EntityManager em;

    public List<RequestStatisticDto> getRequestStatistic(RequestQueryParamDto qp) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<RequestStatisticDto> query = builder.createQuery(RequestStatisticDto.class);
        Root<Request> root = query.from(Request.class);
        if (qp.isUnique()) {
            query.multiselect(root.get("application"), root.get("uri"), builder.countDistinct(root.get("ip")));
            query.orderBy(builder.desc(builder.countDistinct(root.get("ip"))));
        } else {
            query.multiselect(root.get("application"), root.get("uri"), builder.count(root));
            query.orderBy(builder.desc(builder.count(root)));
        }
        Predicate betweenDates = builder.between(root.get("timestamp"), qp.getStart(), qp.getEnd());
        if (qp.getUris() == null) {
            query.where(betweenDates);
        } else {
            Predicate inUrisList = root.get("uri").in(Arrays.asList(qp.getUris()));
            query.where(builder.and(betweenDates, inUrisList));
        }
        query.groupBy(root.get("application"), root.get("uri"));

        return em.createQuery(query).getResultList();
    }
}
