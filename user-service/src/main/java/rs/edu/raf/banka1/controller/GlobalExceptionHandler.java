package rs.edu.raf.banka1.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.tinylog.Logger;
import rs.edu.raf.banka1.exceptions.*;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        Logger.info("Error: Data integrity violation upon update or insert");
        return new ResponseEntity<>("Data integrity violation upon update or insert", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException e) {
        Logger.info("Error: Attempted to access a non-existent value in Optional");
        return new ResponseEntity<>("Attempted to access a non-existent value in Optional", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderNotFoundByIdException.class)
    public ResponseEntity<String> handleOrderNotFoundByIdException(OrderNotFoundByIdException e) {
        Logger.info("Error: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<String> handleEmployeeNotFoundException(EmployeeNotFoundException e) {
        Logger.info("Error: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BankAccountNotFoundException.class)
    public ResponseEntity<String> handleBankAccountNotFoundException(BankAccountNotFoundException e) {
        Logger.info("Error: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CapitalNotFoundByCodeException.class)
    public ResponseEntity<String> handleCapitalNotFoundByCodeException(CapitalNotFoundByCodeException e) {
        Logger.info("Error: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CapitalNotFoundByListingIdAndTypeException.class)
    public ResponseEntity<String> handleCapitalNotFoundByListingIdAndTypeException(CapitalNotFoundByListingIdAndTypeException e) {
        Logger.info("Error: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotEnoughCapitalAvailableException.class)
    public ResponseEntity<String> handleNotEnoughCapitalAvailableException(NotEnoughCapitalAvailableException e) {
        Logger.info("Error: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidReservationAmountException.class)
    public ResponseEntity<String> handleInvalidReservationAmountException(InvalidReservationAmountException e) {
        Logger.info("Error: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCapitalAmountException.class)
    public ResponseEntity<String> handleInvalidCapitalAmountException(InvalidCapitalAmountException e) {
        Logger.info("Error: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<String> handleForbiddenException(ForbiddenException e) {
        Logger.info("Error: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CapitalNotFoundByBankAccountException.class)
    public ResponseEntity<String> handleCapitalNotFoundByBankAccountException(CapitalNotFoundByBankAccountException e) {
        Logger.info("Error: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidOrderListingAmountException.class)
    public ResponseEntity<String> handleInvalidOrderListingAmountException(InvalidOrderListingAmountException e) {
        Logger.info("Error: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderListingNotFoundByIdException.class)
    public ResponseEntity<String> handleOrderListingNotFoundByIdException(OrderListingNotFoundByIdException e) {
        Logger.info("Error: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
