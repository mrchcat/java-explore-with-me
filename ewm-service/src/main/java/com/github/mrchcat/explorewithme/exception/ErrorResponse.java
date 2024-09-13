package com.github.mrchcat.explorewithme.exception;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse(HttpStatus status,
                            String reason,
                            String message,
                            LocalDateTime timestamp,
                            StackTraceElement[] errors) {
}
