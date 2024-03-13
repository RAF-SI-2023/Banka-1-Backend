package rs.edu.raf.banka1.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        // TODO proper logging (?)
        System.out.println("Error: email address must be unique");
        return new ResponseEntity<>("Email address must be unique", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException e) {
        // TODO proper logging
        System.out.println("Error: Attempted to access a non-existent value in Optional");
        return new ResponseEntity<>("Attempted to access a non-existent value in Optional", HttpStatus.NOT_FOUND);
    }
}
