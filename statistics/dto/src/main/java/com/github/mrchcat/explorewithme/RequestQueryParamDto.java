package com.github.mrchcat.explorewithme;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@ToString
@Getter
public class RequestQueryParamDto {
    LocalDateTime start;
    LocalDateTime end;
    String[] uris;
    boolean unique;
}
