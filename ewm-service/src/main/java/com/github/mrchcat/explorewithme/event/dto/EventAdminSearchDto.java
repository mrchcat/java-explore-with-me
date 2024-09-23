package com.github.mrchcat.explorewithme.event.dto;

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
public class EventAdminSearchDto {
    List<Long> userIds;
    List<EventState> states;
    List<Long> categoryIds;
    LocalDateTime start;
    LocalDateTime end;
    Pageable pageable;

}
