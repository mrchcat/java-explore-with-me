package com.github.mrchcat.explorewithme.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.mrchcat.explorewithme.annotation.NotEarlierThan;
import com.github.mrchcat.explorewithme.event.model.EventAdminStateAction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class EventAdminUpdateDto extends EventUpdateDto {
    private EventAdminStateAction stateAction;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotEarlierThan(1)
    private LocalDateTime eventDate;
}
