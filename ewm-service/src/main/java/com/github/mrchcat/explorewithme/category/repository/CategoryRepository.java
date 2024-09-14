package com.github.mrchcat.explorewithme.category.repository;

import com.github.mrchcat.explorewithme.category.model.Category;
import com.github.mrchcat.explorewithme.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

//    @Query("""
//            UPDATE Category AS ctg
//            SET ctg.name=:#{#upd.name}
//            WHERE ctg.id=:#{#upd.id}
//            """)
//    Category updateById(Category upd);

    boolean existsByName(String name);

    @Query("""
            SELECT CASE WHEN COUNT(ctg)>0 THEN TRUE ELSE FALSE END
            FROM Category AS ctg
            WHERE ctg.id!=:id AND ctg.name=:name
            """)
    boolean existsByNameExclId(long id, String name);

    @Query(value = """
            SELECT *
            FROM categories
            LIMIT :size
            OFFSET :from
            """, nativeQuery = true)
    List<Category> getAllCategories(long from, long size);

}
