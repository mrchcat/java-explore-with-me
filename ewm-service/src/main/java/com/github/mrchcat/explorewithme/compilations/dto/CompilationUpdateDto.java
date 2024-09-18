package com.github.mrchcat.explorewithme.compilations.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CompilationUpdateDto {
    private String title;
    private Set<Long> events;
    private Boolean isPinned;
}
