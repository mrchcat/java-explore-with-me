package com.github.mrchcat.explorewithme.compilation.service;

import com.github.mrchcat.explorewithme.compilation.dto.CompilationCreateDto;
import com.github.mrchcat.explorewithme.compilation.dto.CompilationDto;
import com.github.mrchcat.explorewithme.compilation.dto.CompilationUpdateDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompilationService {

    CompilationDto create(CompilationCreateDto createDto);

    void delete(long compilationId);

    CompilationDto update(long compilationId, CompilationUpdateDto updateDto);

    List<CompilationDto> getAllDto(Boolean pinned, Pageable pageable);

    CompilationDto getDtoById(long compilationId);
}
