package com.github.mrchcat.explorewithme.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class LocationCreateDto {
    double lat;
    double lon;
}
