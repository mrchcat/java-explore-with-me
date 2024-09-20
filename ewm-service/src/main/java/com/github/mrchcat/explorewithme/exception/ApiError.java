package com.github.mrchcat.explorewithme.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Builder
public record ApiError(HttpStatus status,
                       String reason,
                       String message,

                       @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                            LocalDateTime timestamp,

                       StackTraceElement[] errors) {
}
