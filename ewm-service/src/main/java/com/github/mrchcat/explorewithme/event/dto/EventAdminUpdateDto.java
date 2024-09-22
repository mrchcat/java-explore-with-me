package com.github.mrchcat.explorewithme.event.dto;

import com.github.mrchcat.explorewithme.event.model.EventAdminStateAction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class EventAdminUpdateDto extends EventUpdateDto {
    private EventAdminStateAction stateAction;
}
