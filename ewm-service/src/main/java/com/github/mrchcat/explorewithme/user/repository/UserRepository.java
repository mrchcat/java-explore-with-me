package com.github.mrchcat.explorewithme.user.repository;

import com.github.mrchcat.explorewithme.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    @Query(value = """
            SELECT *
            FROM users AS u
            LIMIT :size
            OFFSET :from
            """, nativeQuery = true)
    List<User> getAllUsers(long from, long size);

}
