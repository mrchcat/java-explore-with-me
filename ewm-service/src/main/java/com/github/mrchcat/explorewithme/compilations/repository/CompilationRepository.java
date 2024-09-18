package com.github.mrchcat.explorewithme.compilations.repository;

import com.github.mrchcat.explorewithme.compilations.model.Compilation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
}
