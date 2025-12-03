package meli.jestebandev.infrastructure.exception;

import meli.jestebandev.domain.exception.ItemNotFoundException;
import meli.jestebandev.domain.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private ServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/items/MLU123456789")
                .build();
        exchange = MockServerWebExchange.from(request);
    }

    @Test
    void handleItemNotFound_ShouldReturnNotFoundWithCOD001() {
        // Arrange
        ItemNotFoundException exception = new ItemNotFoundException("MLU123456789");

        // Act
        Mono<ResponseEntity<ErrorResponse>> result = exceptionHandler.handleItemNotFound(exception, exchange);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().getErrorCode()).isEqualTo("COD001");
                    assertThat(response.getBody().getStatus()).isEqualTo(404);
                    assertThat(response.getBody().getMessage()).contains("MLU123456789");
                    assertThat(response.getBody().getPath()).isEqualTo("/api/items/MLU123456789");
                })
                .verifyComplete();
    }

    @Test
    void handleValidation_ShouldReturnBadRequestWithCOD002() {
        // Arrange
        ValidationException exception = new ValidationException("Invalid item ID format");

        // Act
        Mono<ResponseEntity<ErrorResponse>> result = exceptionHandler.handleValidation(exception, exchange);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().getErrorCode()).isEqualTo("COD002");
                    assertThat(response.getBody().getStatus()).isEqualTo(400);
                    assertThat(response.getBody().getMessage()).isEqualTo("Invalid item ID format");
                })
                .verifyComplete();
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerErrorWithCOD003() {
        // Arrange
        Exception exception = new RuntimeException("Unexpected error");

        // Act
        Mono<ResponseEntity<ErrorResponse>> result = exceptionHandler.handleGenericException(exception, exchange);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().getErrorCode()).isEqualTo("COD003");
                    assertThat(response.getBody().getStatus()).isEqualTo(500);
                    assertThat(response.getBody().getMessage()).isEqualTo("An internal error has occurred. Please try again later.");
                })
                .verifyComplete();
    }

    @Test
    void handleBindException_ShouldReturnBadRequestWithCOD004() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/items/search?page=-1")
                .build();
        ServerWebExchange exchangeWithParams = MockServerWebExchange.from(request);
        
        org.springframework.validation.BeanPropertyBindingResult bindingResult = 
                new org.springframework.validation.BeanPropertyBindingResult(new Object(), "testObject");
        
        org.springframework.core.MethodParameter methodParameter = 
                new org.springframework.core.MethodParameter(
                        getClass().getDeclaredMethods()[0], -1
                );
        
        WebExchangeBindException exception = new WebExchangeBindException(methodParameter, bindingResult);

        // Act
        Mono<ResponseEntity<ErrorResponse>> result = exceptionHandler.handleBindException(exception, exchangeWithParams);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().getErrorCode()).isEqualTo("COD004");
                    assertThat(response.getBody().getStatus()).isEqualTo(400);
                    assertThat(response.getBody().getMessage()).isEqualTo("Validation error in parameters");
                })
                .verifyComplete();
    }

    @Test
    void errorResponse_ShouldContainTimestampAndPath() {
        // Arrange
        ItemNotFoundException exception = new ItemNotFoundException("MLU123456789");

        // Act
        Mono<ResponseEntity<ErrorResponse>> result = exceptionHandler.handleItemNotFound(exception, exchange);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().getTimestamp()).isNotNull();
                    assertThat(response.getBody().getPath()).isNotNull();
                    assertThat(response.getBody().getError()).isNotNull();
                })
                .verifyComplete();
    }
}

