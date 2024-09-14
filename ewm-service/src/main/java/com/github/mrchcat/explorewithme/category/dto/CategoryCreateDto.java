package com.github.mrchcat.explorewithme.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CategoryCreateDto {
    @NotBlank(message="Field: name. Error: must not be blank. Value: null")
    private String name;
}
