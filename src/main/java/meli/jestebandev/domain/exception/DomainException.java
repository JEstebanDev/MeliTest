package meli.jestebandev.domain.exception;

public abstract class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(validateMessage(message));
    }

    public DomainException(String message, Throwable cause) {
        super(validateMessage(message), cause);
    }
    
    private static String validateMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "An error occurred";
        }
        return message;
    }
}

