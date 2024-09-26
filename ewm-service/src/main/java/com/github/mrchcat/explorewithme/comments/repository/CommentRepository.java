package com.github.mrchcat.explorewithme.comments.repository;

import com.github.mrchcat.explorewithme.comments.model.Comment;
import com.github.mrchcat.explorewithme.event.model.EventState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {

    @Query("""
            SELECT CASE WHEN COUNT(cmt)>0 THEN TRUE ELSE FALSE END
            FROM Comment AS cmt
            WHERE cmt.author.id=:author_id AND cmt.event.id=:eventId
            """)
    boolean existByEventAndAuthor(long eventId, long author_id);

    @Modifying
    @Query("""
            DELETE FROM Comment AS cmt
            JOIN Event AS e ON cmt.event.id=e.id
            WHERE e.state=:state AND cmt.id=:commentId AND cmt.author.id=:userId
            """)
    int deleteByIdAndUserAndState(long commentId, long userId, EventState state);
}
