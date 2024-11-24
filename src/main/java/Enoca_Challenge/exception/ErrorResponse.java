package Enoca_Challenge.exception;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        String message,
        HttpStatus status,
        long timestamp,
        String path
) {
}
