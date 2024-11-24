package Enoca_Challenge.exception;

import Enoca_Challenge.exception.custom.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GeneralExceptionAdvice extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GeneralExceptionAdvice.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest()
                .body(errors);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest req) {
        log.error("Internal server error: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(
                "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, System.currentTimeMillis(), req.getRequestURI()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException ex, HttpServletRequest req) {
        return new ResponseEntity<>(new ErrorResponse(
                ex.getMessage(), HttpStatus.NOT_FOUND, System.currentTimeMillis(), req.getRequestURI()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<ErrorResponse> handleEmptyCartException(EmptyCartException ex, HttpServletRequest req) {
        return new ResponseEntity<>(new ErrorResponse(
                ex.getMessage(), HttpStatus.BAD_REQUEST, System.currentTimeMillis(), req.getRequestURI()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStockException(InsufficientStockException ex, HttpServletRequest req) {
        return new ResponseEntity<>(new ErrorResponse
                (ex.getMessage(), HttpStatus.BAD_REQUEST, System.currentTimeMillis(), req.getRequestURI()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderNotFoundException ex, HttpServletRequest req) {
        return new ResponseEntity<>(new ErrorResponse(
                ex.getMessage(), HttpStatus.NOT_FOUND, System.currentTimeMillis(), req.getRequestURI()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFoundException(CustomerNotFoundException ex, HttpServletRequest req) {
        return new ResponseEntity<>(new ErrorResponse(
                ex.getMessage(), HttpStatus.NOT_FOUND, System.currentTimeMillis(), req.getRequestURI()),
                HttpStatus.NOT_FOUND);
    }
}
