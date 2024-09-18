package com.github.mrchcat.explorewithme.compilation.service;

import com.github.mrchcat.explorewithme.compilation.dto.CompilationCreateDto;
import com.github.mrchcat.explorewithme.compilation.dto.CompilationDto;
import com.github.mrchcat.explorewithme.compilation.dto.CompilationUpdateDto;
import com.github.mrchcat.explorewithme.compilation.mapper.CompilationMapper;
import com.github.mrchcat.explorewithme.compilation.model.Compilation;
import com.github.mrchcat.explorewithme.compilation.repository.CompilationRepository;
import com.github.mrchcat.explorewithme.event.service.EventService;
import com.github.mrchcat.explorewithme.exception.ObjectNotFoundException;
import com.github.mrchcat.explorewithme.validator.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
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

    @Override
    public List<CompilationDto> getAllDto(Boolean pinned, Pageable pageable) {
        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationRepository.findAll(pageable).getContent();
        } else {
            compilations = compilationRepository.findAllByIsPinned(pinned, pageable);
        }
        return compilationMapper.toDto(compilations);
    }

    @Override
    public CompilationDto getDtoById(long compilationId) {
        return compilationMapper.toDto(getById(compilationId));
    }

}
