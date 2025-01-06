// exception/GlobalExceptionHandler.java
package com.example.hnb_chess.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GameException.class)
    public ResponseEntity<String> handleGameException(GameException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(PlayerException.class)
    public ResponseEntity<String> handlePlayerException(PlayerException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        return ResponseEntity.internalServerError().body("An unexpected error occurred");
    }
}