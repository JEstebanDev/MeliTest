package meli.jestebandev.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;
import meli.jestebandev.domain.exception.ItemNotFoundException;
import meli.jestebandev.domain.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ItemNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleItemNotFound(
            ItemNotFoundException ex,
            ServerWebExchange exchange
    ) {
        String requestId = exchange.getRequest().getId();
        String method = exchange.getRequest().getMethod().toString();
        String path = exchange.getRequest().getPath().value();
        
        log.warn("[RequestID: {}] [Method: {}] [Path: {}] Item not found: {}", 
                requestId, method, path, ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(path)
                .errorCode("COD001")
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBindException(
            WebExchangeBindException ex,
            ServerWebExchange exchange
    ) {
        String requestId = exchange.getRequest().getId();
        String method = exchange.getRequest().getMethod().toString();
        String path = exchange.getRequest().getPath().value();
        
        log.warn("[RequestID: {}] [Method: {}] [Path: {}] Validation error: {}", 
                requestId, method, path, ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation error in parameters")
                .path(path)
                .errorCode("COD004")
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
    }

    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidation(
            ValidationException ex,
            ServerWebExchange exchange
    ) {
        String requestId = exchange.getRequest().getId();
        String method = exchange.getRequest().getMethod().toString();
        String path = exchange.getRequest().getPath().value();
        
        log.warn("[RequestID: {}] [Method: {}] [Path: {}] Validation error detected: {}", 
                requestId, method, path, ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(path)
                .errorCode("COD002")
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(
            Exception ex,
            ServerWebExchange exchange
    ) {
        String requestId = exchange.getRequest().getId();
        String method = exchange.getRequest().getMethod().toString();
        String path = exchange.getRequest().getPath().value();
        
        log.error("[RequestID: {}] [Method: {}] [Path: {}] Internal server error: {}", 
                requestId, method, path, ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An internal error has occurred. Please try again later.")
                .path(path)
                .errorCode("COD003")
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
}

