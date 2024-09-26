package com.github.mrchcat.explorewithme.compilation.mapper;

import com.github.mrchcat.explorewithme.compilation.dto.CompilationDto;
import com.github.mrchcat.explorewithme.compilation.model.Compilation;
import com.github.mrchcat.explorewithme.event.dto.EventShortDto;

import java.util.List;

public class CompilationMapper {

    public static CompilationDto toDto(Compilation compilation, List<EventShortDto> eventShortDtos) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getIsPinned())
                .events(eventShortDtos)
                .build();
    }
}
