package meli.jestebandev.domain.port.out;

public interface InputValidator {

    String validateItemId(String id);

    String validateSearchQuery(String query);

    String validateCategory(String category);

    void validatePagination(int page, int size);
}

