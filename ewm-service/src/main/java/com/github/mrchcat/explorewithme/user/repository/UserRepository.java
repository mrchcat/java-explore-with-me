package com.github.mrchcat.explorewithme.user.repository;

import com.github.mrchcat.explorewithme.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    @Query(value = """
            SELECT *
            FROM users
            LIMIT :size
            OFFSET :from
            """, nativeQuery = true)
    List<User> getAllUsers(long from, long size);

    @Query(value = """
            SELECT *
            FROM users
            WHERE id IN (:userIds)
            LIMIT :size
            OFFSET :from
            """, nativeQuery = true)
    List<User> getSelectedUsers(List<Long> userIds, long from, long size);
}
