package com.github.mrchcat.explorewithme.compilation.mapper;

import com.github.mrchcat.explorewithme.compilation.dto.CompilationCreateDto;
import com.github.mrchcat.explorewithme.compilation.dto.CompilationDto;
import com.github.mrchcat.explorewithme.compilation.dto.CompilationUpdateDto;
import com.github.mrchcat.explorewithme.compilation.model.Compilation;
import com.github.mrchcat.explorewithme.event.mapper.EventMapper;
import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CompilationMapper {
    private final EventService eventService;
    private final EventMapper eventMapper;

    public Compilation toEntity(Set<Long> eventIds, CompilationCreateDto createDto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(createDto.getTitle());
        Boolean isPinned = createDto.getPinned();
        if (isPinned != null) {
            compilation.setIsPinned(isPinned);
        }
        if (eventIds != null && !eventIds.isEmpty()) {
            compilation.getEvents().addAll(eventService.getById(eventIds.stream().toList()));
        }
        return compilation;
    }

    public Compilation toEntity(Compilation compilation, CompilationUpdateDto updateDto) {
        String title = updateDto.getTitle();
        if (title != null && !title.isEmpty()) {
            compilation.setTitle(title);
        }
        Boolean isPinned = updateDto.getPinned();
        if (isPinned != null) {
            compilation.setIsPinned(isPinned);
        }
        Set<Long> eventIds = updateDto.getEvents();
        if (eventIds != null) {
            var oldEvents = compilation.getEvents();
            oldEvents.clear();
            oldEvents.addAll(eventService.getById(eventIds.stream().toList()));
        }
        return compilation;
    }


    public CompilationDto toDto(Compilation compilation) {
        List<Event> events = compilation.getEvents().stream().toList();
        var eventShortDtos = eventMapper.toShortDto(events);
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getIsPinned())
                .events(eventShortDtos)
                .build();
    }

    public List<CompilationDto> toDto(List<Compilation> compilations) {
        return compilations.stream()
                .map(this::toDto)
                .toList();
    }


}
