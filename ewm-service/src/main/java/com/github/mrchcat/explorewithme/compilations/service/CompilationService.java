package com.github.mrchcat.explorewithme.compilations.service;

import com.github.mrchcat.explorewithme.compilations.dto.CompilationCreateDto;
import com.github.mrchcat.explorewithme.compilations.dto.CompilationDto;
import com.github.mrchcat.explorewithme.compilations.dto.CompilationUpdateDto;
import com.github.mrchcat.explorewithme.compilations.model.Compilation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompilationService {

    CompilationDto create(CompilationCreateDto createDto);

    void delete(long compilationId);

    CompilationDto update(long compilationId, CompilationUpdateDto updateDto);

    Compilation getById(long compilationId);

    List<CompilationDto> getAllDto(Boolean pinned, Pageable pageable);

    CompilationDto getDtoById(long compilationId);
}
