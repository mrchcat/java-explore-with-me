package com.github.mrchcat.explorewithme;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class RequestStatisticDTO {
    String app;
    String uri;
    long hits;
}
