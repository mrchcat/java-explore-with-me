package com.github.mrchcat.explorewithme.event.dto;

import com.github.mrchcat.explorewithme.event.model.EventUserStateAction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class EventPrivateUpdateDto extends EventUpdateDto {
    private EventUserStateAction stateAction;
}
