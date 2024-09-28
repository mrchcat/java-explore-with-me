package com.github.mrchcat.explorewithme.comments.dto;

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
public class CommentPrivateCreateDto {
    @NotBlank(message = "Comment can not be empty")
    @Length(min = 1, max = 10000, message = "Comment must have from 1 to 10000 signs")
    private String text;
}
