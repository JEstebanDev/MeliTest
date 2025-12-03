package meli.jestebandev.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PaginatedResult Unit Tests")
class PaginatedResultTest {

    @Test
    @DisplayName("Should correctly calculate total pages")
    void shouldCalculateTotalPages() {
        // División exacta
        List<String> content = Arrays.asList("item1", "item2", "item3");
        PaginatedResult<String> result1 = new PaginatedResult<>(content, 50L, 0, 10);
        assertThat(result1.totalPages()).isEqualTo(5);

        // División inexacta
        PaginatedResult<String> result2 = new PaginatedResult<>(content, 23L, 0, 10);
        assertThat(result2.totalPages()).isEqualTo(3);

        // Sin elementos
        PaginatedResult<String> result3 = new PaginatedResult<>(Collections.emptyList(), 0L, 0, 10);
        assertThat(result3.totalPages()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should correctly determine if has next page")
    void shouldDetermineHasNext() {
        List<String> content = Arrays.asList("item1", "item2");
        
        // Primera página de varias
        PaginatedResult<String> result1 = new PaginatedResult<>(content, 30L, 0, 10);
        assertThat(result1.hasNext()).isTrue();

        // Última página
        PaginatedResult<String> result2 = new PaginatedResult<>(content, 30L, 2, 10);
        assertThat(result2.hasNext()).isFalse();

        // Única página
        PaginatedResult<String> result3 = new PaginatedResult<>(content, 5L, 0, 10);
        assertThat(result3.hasNext()).isFalse();
    }

    @Test
    @DisplayName("Should correctly determine if has previous page")
    void shouldDetermineHasPrevious() {
        List<String> content = Arrays.asList("item1", "item2");
        
        // Primera página
        PaginatedResult<String> result1 = new PaginatedResult<>(content, 30L, 0, 10);
        assertThat(result1.hasPrevious()).isFalse();

        // Página intermedia
        PaginatedResult<String> result2 = new PaginatedResult<>(content, 30L, 1, 10);
        assertThat(result2.hasPrevious()).isTrue();

        // Última página
        PaginatedResult<String> result3 = new PaginatedResult<>(content, 50L, 4, 10);
        assertThat(result3.hasPrevious()).isTrue();
    }

    @Test
    @DisplayName("Should correctly determine if is first page")
    void shouldDetermineIsFirst() {
        List<String> content = Arrays.asList("item1", "item2");
        
        // Primera página
        PaginatedResult<String> result1 = new PaginatedResult<>(content, 30L, 0, 10);
        assertThat(result1.isFirst()).isTrue();

        // Otra página
        PaginatedResult<String> result2 = new PaginatedResult<>(content, 30L, 1, 10);
        assertThat(result2.isFirst()).isFalse();
    }

    @Test
    @DisplayName("Should correctly determine if is last page")
    void shouldDetermineIsLast() {
        List<String> content = Arrays.asList("item1", "item2");
        
        // Primera página (no es última)
        PaginatedResult<String> result1 = new PaginatedResult<>(content, 30L, 0, 10);
        assertThat(result1.isLast()).isFalse();

        // Última página
        PaginatedResult<String> result2 = new PaginatedResult<>(content, 30L, 2, 10);
        assertThat(result2.isLast()).isTrue();

        // Única página
        PaginatedResult<String> result3 = new PaginatedResult<>(content, 5L, 0, 10);
        assertThat(result3.isLast()).isTrue();
    }

    @Test
    @DisplayName("Should have correct values for first page of multiple pages")
    void shouldHaveCorrectValuesForFirstPage() {
        List<String> content = Arrays.asList("item1", "item2", "item3", "item4", "item5",
                "item6", "item7", "item8", "item9", "item10");
        PaginatedResult<String> result = new PaginatedResult<>(content, 100L, 0, 10);

        assertThat(result.totalPages()).isEqualTo(10);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.hasPrevious()).isFalse();
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isFalse();
    }

    @Test
    @DisplayName("Should have correct values for middle page")
    void shouldHaveCorrectValuesForMiddlePage() {
        List<String> content = Arrays.asList("item51", "item52", "item53", "item54", "item55",
                "item56", "item57", "item58", "item59", "item60");
        PaginatedResult<String> result = new PaginatedResult<>(content, 100L, 5, 10);

        assertThat(result.totalPages()).isEqualTo(10);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.hasPrevious()).isTrue();
        assertThat(result.isFirst()).isFalse();
        assertThat(result.isLast()).isFalse();
    }

    @Test
    @DisplayName("Should have correct values for single page")
    void shouldHaveCorrectValuesForSinglePage() {
        List<String> content = Arrays.asList("item1", "item2", "item3", "item4", "item5");
        PaginatedResult<String> result = new PaginatedResult<>(content, 5L, 0, 10);

        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.hasPrevious()).isFalse();
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();
    }
}
