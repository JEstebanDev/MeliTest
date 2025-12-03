package meli.jestebandev.infrastructure.adapter.out.validation;

import meli.jestebandev.domain.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SecurityInputValidator Tests")
class SecurityInputValidatorTest {

    private SecurityInputValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SecurityInputValidator();
    }

    @Test
    @DisplayName("Should validate correct Mercado Libre ID")
    void shouldValidateCorrectMercadoLibreId() {
        String validId = "MLU123456789";
        String result = validator.validateItemId(validId);
        assertThat(result).isEqualTo("MLU123456789");
    }

    @Test
    @DisplayName("Should throw exception for invalid item ID")
    void shouldThrowExceptionForInvalidItemId() {
        assertThatThrownBy(() -> validator.validateItemId(null))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Item ID cannot be null or empty");

        assertThatThrownBy(() -> validator.validateItemId(""))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Item ID cannot be null or empty");

        assertThatThrownBy(() -> validator.validateItemId("MLU" + "1".repeat(50)))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("exceeds maximum length");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123456789",
            "ML123456789",
            "<script>alert('xss')</script>",
            "MLU123; DROP TABLE items;"
    })
    @DisplayName("Should reject invalid ID formats")
    void shouldRejectInvalidIdFormats(String invalidId) {
        assertThatThrownBy(() -> validator.validateItemId(invalidId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Item ID format is invalid");
    }

    @Test
    @DisplayName("Should validate normal search query")
    void shouldValidateNormalSearchQuery() {
        String query = "laptop gaming";
        String result = validator.validateSearchQuery(query);
        assertThat(result).isEqualTo("laptop gaming");
    }

    @Test
    @DisplayName("Should return null for empty or null search query")
    void shouldReturnNullForEmptySearchQuery() {
        assertThat(validator.validateSearchQuery(null)).isNull();
        assertThat(validator.validateSearchQuery("")).isNull();
        assertThat(validator.validateSearchQuery("   ")).isNull();
    }

    @Test
    @DisplayName("Should escape HTML characters in search query")
    void shouldEscapeHtmlCharactersInQuery() {
        String queryWithHtml = "laptop <b>bold</b> & \"quoted\"";
        String result = validator.validateSearchQuery(queryWithHtml);
        
        assertThat(result).contains("&lt;b&gt;");
        assertThat(result).contains("&amp;");
        assertThat(result).contains("&quot;");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "<script>alert('XSS')</script>",
            "javascript:alert('XSS')",
            "<img src=x onerror=alert('XSS')>"
    })
    @DisplayName("Should detect and reject XSS attempts")
    void shouldDetectAndRejectXssAttempts(String xssQuery) {
        assertThatThrownBy(() -> validator.validateSearchQuery(xssQuery))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Search query contains invalid characters or patterns");
    }

    @Test
    @DisplayName("Should validate correct category")
    void shouldValidateCorrectCategory() {
        String category = "MLA1648";
        String result = validator.validateCategory(category);
        assertThat(result).isEqualTo("MLA1648");
    }

    @Test
    @DisplayName("Should return null for empty or null category")
    void shouldReturnNullForEmptyCategory() {
        assertThat(validator.validateCategory(null)).isNull();
        assertThat(validator.validateCategory("")).isNull();
        assertThat(validator.validateCategory("   ")).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "category with spaces",
            "category@email",
            "category<script>",
            "category/slash"
    })
    @DisplayName("Should reject categories with invalid characters")
    void shouldRejectCategoriesWithInvalidCharacters(String invalidCategory) {
        assertThatThrownBy(() -> validator.validateCategory(invalidCategory))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Category ID contains invalid characters");
    }

    @Test
    @DisplayName("Should validate correct pagination parameters")
    void shouldValidateCorrectPaginationParameters() {
        validator.validatePagination(0, 10);
        validator.validatePagination(0, 1);
        validator.validatePagination(1000, 100);
    }

    @Test
    @DisplayName("Should throw exception for invalid pagination parameters")
    void shouldThrowExceptionForInvalidPagination() {
        assertThatThrownBy(() -> validator.validatePagination(-1, 10))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Page number cannot be negative");

        assertThatThrownBy(() -> validator.validatePagination(0, 0))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Page size must be at least 1");

        assertThatThrownBy(() -> validator.validatePagination(0, 101))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Page size cannot exceed 100 items");
    }
}
