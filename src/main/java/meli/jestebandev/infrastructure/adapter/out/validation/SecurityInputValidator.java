package meli.jestebandev.infrastructure.adapter.out.validation;

import meli.jestebandev.domain.exception.ValidationException;
import meli.jestebandev.domain.port.out.InputValidator;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class SecurityInputValidator implements InputValidator {

    private static final int MAX_QUERY_LENGTH = 200;
    private static final int MAX_CATEGORY_LENGTH = 50;
    private static final int MAX_ITEM_ID_LENGTH = 50;
    private static final int MIN_PAGE_SIZE = 1;
    private static final int MAX_PAGE_SIZE = 100;
    
    // Pattern for Mercado Libre item IDs (e.g., MLU123456789, MLA987654321)
    private static final Pattern ITEM_ID_PATTERN = Pattern.compile("^ML[A-Z]{1,3}\\d+$");
    
    // Pattern for category IDs (alphanumeric with optional hyphens/underscores)
    private static final Pattern CATEGORY_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");

    @Override
    public String validateItemId(String id) {
        if (id == null || id.isBlank()) {
            throw new ValidationException("Item ID cannot be null or empty");
        }

        String trimmed = id.trim();
        
        // Validate length
        if (trimmed.length() > MAX_ITEM_ID_LENGTH) {
            throw new ValidationException(
                String.format("Item ID exceeds maximum length of %d characters", MAX_ITEM_ID_LENGTH)
            );
        }

        // Validate Mercado Libre ID format (e.g., MLU123456789)
        if (!ITEM_ID_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException(
                "Item ID format is invalid. Expected format: ML[A-Z]{1-3}[digits] (e.g., MLU123456789)"
            );
        }

        return trimmed;
    }

    @Override
    public String validateSearchQuery(String query) {
        if (query == null || query.isBlank()) {
            return null;
        }

        String trimmed = query.trim();
        
        // Validate length
        if (trimmed.length() > MAX_QUERY_LENGTH) {
            throw new ValidationException(
                String.format("Search query exceeds maximum length of %d characters", MAX_QUERY_LENGTH)
            );
        }

        // Check for suspicious patterns that might indicate XSS attempts
        if (containsScriptTags(trimmed)) {
            throw new ValidationException("Search query contains invalid characters or patterns");
        }

        // Escape HTML to prevent XSS
        return StringEscapeUtils.escapeHtml4(trimmed);
    }

    @Override
    public String validateCategory(String category) {
        if (category == null || category.isBlank()) {
            return null;
        }

        String trimmed = category.trim();
        
        // Validate length
        if (trimmed.length() > MAX_CATEGORY_LENGTH) {
            throw new ValidationException(
                String.format("Category ID exceeds maximum length of %d characters", MAX_CATEGORY_LENGTH)
            );
        }

        // Validate format (alphanumeric with optional hyphens/underscores)
        if (!CATEGORY_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException(
                "Category ID contains invalid characters. Only alphanumeric, hyphens, and underscores are allowed"
            );
        }

        return trimmed;
    }

    @Override
    public void validatePagination(int page, int size) {
        if (page < 0) {
            throw new ValidationException("Page number cannot be negative");
        }

        if (size < MIN_PAGE_SIZE) {
            throw new ValidationException(
                String.format("Page size must be at least %d", MIN_PAGE_SIZE)
            );
        }

        if (size > MAX_PAGE_SIZE) {
            throw new ValidationException(
                String.format("Page size cannot exceed %d items", MAX_PAGE_SIZE)
            );
        }
    }

    /**
     * Checks if the input contains script tags or common XSS patterns.
     *
     * @param input the string to check
     * @return true if suspicious patterns are found
     */
    private boolean containsScriptTags(String input) {
        String lowerInput = input.toLowerCase();
        return lowerInput.contains("<script") 
            || lowerInput.contains("javascript:") 
            || lowerInput.contains("onerror=")
            || lowerInput.contains("onload=")
            || lowerInput.contains("<iframe");
    }
}

