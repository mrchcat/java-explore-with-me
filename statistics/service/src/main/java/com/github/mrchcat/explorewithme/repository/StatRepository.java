package com.github.mrchcat.explorewithme.repository;

import com.github.mrchcat.explorewithme.model.Request;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface StatRepository extends JpaRepository<Request, Long>, StatCustomRepository {

}
