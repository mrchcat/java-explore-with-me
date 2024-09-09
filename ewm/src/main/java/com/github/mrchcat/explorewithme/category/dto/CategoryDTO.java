package com.github.mrchcat.explorewithme.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CategoryDTO {
    private long id;
    private String name;
}
