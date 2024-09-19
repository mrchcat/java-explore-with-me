package com.github.mrchcat.explorewithme.category.repository;

import com.github.mrchcat.explorewithme.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);

    @Query("""
            SELECT CASE WHEN COUNT(ctg)>0 THEN TRUE ELSE FALSE END
            FROM Category AS ctg
            WHERE ctg.id!=:id AND ctg.name=:name
            """)
    boolean existsByNameExclId(long id, String name);

}
