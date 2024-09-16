package com.github.mrchcat.explorewithme.event.dto;

import com.github.mrchcat.explorewithme.category.model.Category;
import com.github.mrchcat.explorewithme.event.model.EventSortAttribute;
import com.github.mrchcat.explorewithme.event.model.EventState;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventPublicSearchDto {
    String text;
    List<Long> categoryIds;
    Boolean paid;
    LocalDateTime start=LocalDateTime.now();
    LocalDateTime end;
    Boolean onlyAvailable;
    List<EventState> states = List.of(EventState.PUBLISHED);
}
