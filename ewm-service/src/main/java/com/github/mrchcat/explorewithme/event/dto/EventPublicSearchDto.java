package com.github.mrchcat.explorewithme.event.dto;

import com.github.mrchcat.explorewithme.event.model.EventSortAttribute;
import com.github.mrchcat.explorewithme.event.model.EventState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventPublicSearchDto extends EventSearchDto {
    String text;
    List<Long> categoryIds;
    Boolean paid;
    @Builder.Default
    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end;
    Boolean onlyAvailable;
    @Builder.Default
    List<EventState> states = List.of(EventState.PUBLISHED);
    Pageable pageable;
    EventSortAttribute eventSortAttribute;

}
