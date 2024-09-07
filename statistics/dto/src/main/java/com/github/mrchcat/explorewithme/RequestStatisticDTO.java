package com.github.mrchcat.explorewithme;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class RequestStatisticDTO {
    private String app;
    private String uri;
    private long hits;
}
