package com.github.mrchcat.explorewithme.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.mrchcat.explorewithme.annotation.NotEarlierThan;
import com.github.mrchcat.explorewithme.event.model.EventUserStateAction;
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
public class EventPrivateUpdateDto extends EventUpdateDto {
    private EventUserStateAction stateAction;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotEarlierThan(2)
    private LocalDateTime eventDate;
}
