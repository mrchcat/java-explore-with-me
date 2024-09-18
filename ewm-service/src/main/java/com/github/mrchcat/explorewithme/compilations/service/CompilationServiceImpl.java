package com.github.mrchcat.explorewithme.compilations.service;

import com.github.mrchcat.explorewithme.compilations.dto.CompilationCreateDto;
import com.github.mrchcat.explorewithme.compilations.dto.CompilationDto;
import com.github.mrchcat.explorewithme.compilations.dto.CompilationUpdateDto;
import com.github.mrchcat.explorewithme.compilations.mapper.CompilationMapper;
import com.github.mrchcat.explorewithme.compilations.model.Compilation;
import com.github.mrchcat.explorewithme.compilations.repository.CompilationRepository;
import com.github.mrchcat.explorewithme.event.service.EventService;
import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
import com.github.mrchcat.explorewithme.validator.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final Validator validator;
    private final EventService eventService;
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationDto create(CompilationCreateDto createDto) {
        var eventIds = createDto.getEvents();
        validator.isEventExist(eventIds);
        Compilation compilationToSave = compilationMapper.toEntity(eventIds, createDto);
        Compilation savedCompilation = compilationRepository.save(compilationToSave);
        log.info("Compilation created {}", savedCompilation);
        return compilationMapper.toDto(savedCompilation);
    }

    @Override
    public void delete(long compilationId) {
        validator.isCompilationExist(compilationId);
        compilationRepository.deleteById(compilationId);
        log.info("Compilation with id={} deleted", compilationId);
    }

    @Override
    public CompilationDto update(long compilationId, CompilationUpdateDto updateDto) {
        validator.isCompilationExist(compilationId);
        Compilation compilation = getById(compilationId);
        var eventIds = updateDto.getEvents();
        validator.isEventExist(eventIds);
        Compilation compilationToSave = compilationMapper.toEntity(compilation, updateDto);
        Compilation savedCompilation = compilationRepository.save(compilationToSave);
        log.info("Compilation updated to {}", savedCompilation);
        return compilationMapper.toDto(savedCompilation);
    }

    public Compilation getById(long compilationId) {
        Optional<Compilation> compilationOptional = compilationRepository.findById(compilationId);
        return compilationOptional.orElseThrow(() -> {
            String message = String.format("Compilation with id=%d was not found", compilationId);
            return new ObjectNotFoundException(message);
        });
    }

}
