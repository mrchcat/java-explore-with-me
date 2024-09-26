package com.github.mrchcat.explorewithme.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CategoryCreateDto {
    @NotBlank(message = "Field: name. Error: must not be blank. Value: null")
    @Length(min = 1, max = 50, message = "Name must have size 2-250 signs.")
    private String name;
}
