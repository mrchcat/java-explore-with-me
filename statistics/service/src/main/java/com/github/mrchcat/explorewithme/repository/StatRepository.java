package com.github.mrchcat.explorewithme.repository;

import com.github.mrchcat.explorewithme.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatRepository extends JpaRepository<Request, Long>, StatCustomRepository {

}
