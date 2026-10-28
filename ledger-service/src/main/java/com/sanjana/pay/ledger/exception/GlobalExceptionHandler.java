package com.sanjana.pay.ledger.exception;

import com.sanjana.pay.ledger.dto.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.time.Instant;
/**
 * WHAT: catches every error thrown in the controllers and answers with one JSON shape.
 * WHY: (1) clients handle errors in one way, (2) we never leak stack traces or SQL,
 *      (3) the log keeps the full detail for us, the client gets only a safe message.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Our own business errors: the status depends on the type of error.
    @ExceptionHandler(LedgerException.class)
    public ResponseEntity<ErrorResponse> handleLedger(LedgerException e) {
        HttpStatus status = statusFor(e);
        log.warn("Ledger error code={} message={}", e.getCode(), e.getMessage());
        return build(status, e.getCode(), e.getMessage(), List.of());
    }

    // @Valid failed: say which fields are wrong.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        List<String> details = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Request is not valid", details);
    }

    // Broken JSON, wrong type, or an enum value that does not exist (for example "SIDEWAYS").
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException e) {
        return build(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", "Request body is missing or not valid JSON", List.of());
    }

    // The database refused the data (for example a rule or unique key).
    // We never send the SQL text to the client.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleIntegrity(DataIntegrityViolationException e) {
        log.warn("Data integrity violation: {}", e.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT, "DATA_CONFLICT", "The request conflicts with existing data", List.of());
    }

    // Anything we did not expect: log everything, tell the client nothing private.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        log.error("Unexpected error", e);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Something went wrong", List.of());
    }

    private HttpStatus statusFor(LedgerException e) {
        return switch (e) {
            case AccountNotFoundException ex -> HttpStatus.NOT_FOUND;
            case TransactionNotFoundException ex -> HttpStatus.NOT_FOUND;
            case UnbalancedTransactionException ex -> HttpStatus.UNPROCESSABLE_ENTITY;
            case CurrencyMismatchException ex -> HttpStatus.UNPROCESSABLE_ENTITY;
            case PostingKeyConflictException ex -> HttpStatus.CONFLICT;
            case AlreadyReversedException ex -> HttpStatus.CONFLICT;
            default -> HttpStatus.BAD_REQUEST;
        };
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message, List<String> details) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(code, message, Instant.now(), details));
    }

    // URL does not exist: answer 404, not 500.
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException e) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", "No such endpoint", List.of());
    }

    // Right URL but wrong method (for example GET on a POST-only URL): answer 405.
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleWrongMethod(HttpRequestMethodNotSupportedException e) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "This method is not allowed here", List.of());
    }
}