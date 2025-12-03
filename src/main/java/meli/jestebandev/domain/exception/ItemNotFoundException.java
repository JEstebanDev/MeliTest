package meli.jestebandev.domain.exception;

public class ItemNotFoundException extends DomainException {
    
    public ItemNotFoundException(String id) {
        super("Item not found with ID: " + id);
    }
}

