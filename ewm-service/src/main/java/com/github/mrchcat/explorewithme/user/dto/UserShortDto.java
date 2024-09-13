package com.github.mrchcat.explorewithme.user.dto;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class UserShortDto {
    private long id;
    private String name;
}
