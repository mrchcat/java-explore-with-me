package com.github.mrchcat.explorewithme.repository;

import com.github.mrchcat.explorewithme.RequestStatisticDTO;
import com.github.mrchcat.explorewithme.model.Request;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatCustomRepositoryImp implements StatCustomRepository{
    EntityManager em;

    @Override
    public List<RequestStatisticDTO> getRequests(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        CriteriaBuilder builder=em.getCriteriaBuilder();
        CriteriaQuery<RequestStatisticDTO> query = builder.createQuery(RequestStatisticDTO.class);
        Root<Request> root = query.from(Request.class);
        query.multiselect(root.get("application"),root.get("uri"),builder.count(root))
                .distinct(unique);
        Predicate betweenDates=builder.between(root.get("time"),start,end);
        if(uris==null){
            query.where(betweenDates);
        } else{
            query.where(builder.and(betweenDates,root.get("uri").in((Object) uris)));
        }
        return em.createQuery(query).getResultList();
    }
}
