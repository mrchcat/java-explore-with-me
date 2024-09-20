package com.github.mrchcat.explorewithme.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.mrchcat.explorewithme.event.model.EventAdminStateAction;
import com.github.mrchcat.explorewithme.event.model.Location;
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
public class EventAdminUpdateDto {

    @Length(min = 3, max = 120, message = "Title must have size 3-120 signs.")
    private String title;

    @Length(min = 20, max = 2000, message = "Annotation must have size 20-2000 signs.")
    private String annotation;

    @Length(min = 20, max = 7000, message = "Description must have size 20-7000 signs.")
    private String description;

    private Long category;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;
    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    private EventAdminStateAction stateAction;
}