package com.github.mrchcat.explorewithme.compilation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CompilationCreateDto {
    @Length(min = 1, max = 50, message = "Title must have from 1 to 50 symbols")
    private String title;
    private Set<Long> events;
    private Boolean pinned;
}
