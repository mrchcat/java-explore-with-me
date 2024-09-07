package com.github.mrchcat.explorewithme;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class RequestCreateDTO {
    @NotBlank
    String app;
    @NotBlank
    String uri;
    @NotNull
    InetAddress ip;
    @NotNull
    @PastOrPresent
    LocalDateTime timestamp;
}
