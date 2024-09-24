package com.github.mrchcat.explorewithme.compilation.service;

import com.github.mrchcat.explorewithme.compilation.dto.CompilationCreateDto;
import com.github.mrchcat.explorewithme.compilation.dto.CompilationDto;
import com.github.mrchcat.explorewithme.compilation.dto.CompilationUpdateDto;
import com.github.mrchcat.explorewithme.compilation.mapper.CompilationMapper;
import com.github.mrchcat.explorewithme.compilation.model.Compilation;
import com.github.mrchcat.explorewithme.compilation.repository.CompilationRepository;
import com.github.mrchcat.explorewithme.event.dto.EventShortDto;
import com.github.mrchcat.explorewithme.event.mapper.EventMapper;
import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.event.service.EventService;
import com.github.mrchcat.explorewithme.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventService eventService;

    @Override
    public CompilationDto create(CompilationCreateDto createDto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(createDto.getTitle());
        Boolean isPinned = createDto.getPinned();
        if (isPinned != null) {
            compilation.setIsPinned(isPinned);
        }
        List<Event> newEvents = Collections.emptyList();
        Set<Long> eventIds = createDto.getEvents();
        if (eventIds != null) {
            newEvents = eventService.getById(eventIds.stream().toList());
            compilation.getEvents().addAll(newEvents);
        }
        Compilation savedCompilation = compilationRepository.save(compilation);
        log.info("Compilation created {}", savedCompilation);
        List<EventShortDto> eventShortDtos = EventMapper.toShortDto(newEvents, eventService.getEventViews(newEvents));
        return CompilationMapper.toDto(savedCompilation, eventShortDtos);
    }

    @Override
    public void delete(long compilationId) {
        compilationRepository.deleteById(compilationId);
        log.info("Compilation with id={} deleted", compilationId);
    }

    @Transactional
    @Override
    public CompilationDto update(long compilationId, CompilationUpdateDto updateDto) {
        Compilation compilation = getById(compilationId);
        String title = updateDto.getTitle();
        if (title != null && !title.isEmpty()) {
            compilation.setTitle(title);
        }
        Boolean isPinned = updateDto.getPinned();
        if (isPinned != null) {
            compilation.setIsPinned(isPinned);
        }
        Set<Long> eventIds = updateDto.getEvents();
        List<Event> events = Collections.emptyList();
        if (eventIds != null) {
            events = eventService.getById(eventIds.stream().toList());
            compilation.getEvents().clear();
            compilation.getEvents().addAll(events);
        }
        Compilation savedCompilation = compilationRepository.save(compilation);
        log.info("Compilation updated to {}", savedCompilation);
        List<EventShortDto> eventShortDtos = EventMapper.toShortDto(events, eventService.getEventViews(events));
        return CompilationMapper.toDto(savedCompilation, eventShortDtos);
    }

    public Compilation getById(long compilationId) {
        Optional<Compilation> compilationOptional = compilationRepository.findById(compilationId);
        return compilationOptional.orElseThrow(() -> {
            String message = "Compilation with id=" + compilationId + " was not found";
            return new NotFoundException(message);
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
        return compilations.stream()
                .map(comp -> {
                    List<Event> events = comp.getEvents().stream().toList();
                    List<EventShortDto> eventShortDtos = EventMapper.toShortDto(events, eventService.getEventViews(events));
                    return CompilationMapper.toDto(comp, eventShortDtos);
                }).toList();
    }

    @Override
    public CompilationDto getDtoById(long compilationId) {
        Compilation compilation = getById(compilationId);
        List<Event> events = compilation.getEvents().stream().toList();
        List<EventShortDto> eventShortDtos = EventMapper.toShortDto(events, eventService.getEventViews(events));
        return CompilationMapper.toDto(compilation, eventShortDtos);
    }
}
