package com.github.mrchcat.explorewithme.event.dto;

import com.github.mrchcat.explorewithme.exception.ArgumentNotValidException;

import java.time.LocalDateTime;

public class EventSearchDto {
    public static void isCorrectDateOrder(LocalDateTime start, LocalDateTime finish) {
        if (start != null && finish != null && finish.isBefore(start)) {
            String message = "The dates violate order: " + start + " must be before " + finish;
            throw new ArgumentNotValidException(message);
        }
    }
}
