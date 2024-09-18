package com.github.mrchcat.explorewithme.compilations.dto;

import com.github.mrchcat.explorewithme.event.dto.EventShortDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDto {
    private long id;
    private String title;
    private Boolean pinned;
    private List<EventShortDto> events;
}
