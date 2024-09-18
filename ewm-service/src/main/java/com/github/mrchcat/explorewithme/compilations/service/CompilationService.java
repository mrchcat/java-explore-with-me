package com.github.mrchcat.explorewithme.compilations.service;

import com.github.mrchcat.explorewithme.compilations.dto.CompilationCreateDto;
import com.github.mrchcat.explorewithme.compilations.dto.CompilationDto;
import com.github.mrchcat.explorewithme.compilations.dto.CompilationUpdateDto;
import com.github.mrchcat.explorewithme.compilations.model.Compilation;

public interface CompilationService {

    CompilationDto create(CompilationCreateDto createDto);

    void delete(long compilationId);

    CompilationDto update(long compilationId, CompilationUpdateDto updateDto);

    Compilation getById(long id);

}
