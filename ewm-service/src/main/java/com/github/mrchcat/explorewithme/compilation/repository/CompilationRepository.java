package com.github.mrchcat.explorewithme.compilation.repository;

import com.github.mrchcat.explorewithme.compilation.model.Compilation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("""
            SELECT cmp
            FROM Compilation AS cmp
            WHERE cmp.isPinned=:pinned
            """)
    List<Compilation> findAllByIsPinned(Boolean pinned, Pageable pageable);
}
