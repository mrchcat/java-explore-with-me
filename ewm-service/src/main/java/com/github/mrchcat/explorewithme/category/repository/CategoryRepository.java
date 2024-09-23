package com.github.mrchcat.explorewithme.category.repository;

import com.github.mrchcat.explorewithme.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
