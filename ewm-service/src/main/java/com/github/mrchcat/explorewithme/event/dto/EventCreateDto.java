package com.github.mrchcat.explorewithme.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.github.mrchcat.explorewithme.event.model.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class EventCreateDto {

    @NotBlank(message = "title can not be empty")
    private String title;

    @Length(min = 20, max = 2000, message = "Annotation must have size 20-2000 signs.")
    private String annotation;

    @Length(min = 20, max = 7000, message = "Description must have size 20-7000 signs.")
    private String description;

    @NotNull(message = "category can not be empty")
    private Long category;

    @NotNull(message = "event can not be empty")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;
    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    @JsonSetter(nulls = Nulls.SKIP)
    private Boolean requestModeration = true;
}
