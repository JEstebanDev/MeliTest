package meli.jestebandev.application.usecase;

import meli.jestebandev.domain.exception.ValidationException;
import meli.jestebandev.domain.model.Category;
import meli.jestebandev.domain.model.Item;
import meli.jestebandev.domain.model.ItemCondition;
import meli.jestebandev.domain.model.PaginatedResult;
import meli.jestebandev.domain.port.out.InputValidator;
import meli.jestebandev.domain.port.out.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SearchItemsService Unit Tests")
class SearchItemsServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private InputValidator inputValidator;

    @InjectMocks
    private SearchItemsService searchItemsService;

    private List<Item> testItems;

    @BeforeEach
    void setUp() {
        testItems = Arrays.asList(
                Item.builder()
                        .id("MLU001")
                        .title("Laptop Dell")
                        .price(BigDecimal.valueOf(999.99))
                        .condition(ItemCondition.NEW)
                        .category(Category.builder().id("CAT001").name("Computación").build())
                        .build(),
                Item.builder()
                        .id("MLU002")
                        .title("iPhone 15")
                        .price(BigDecimal.valueOf(1199.99))
                        .condition(ItemCondition.NEW)
                        .category(Category.builder().id("CAT002").name("Celulares").build())
                        .build()
        );
    }

    @Test
    @DisplayName("Should return all items when no filters")
    void shouldReturnAllItemsWhenNoFilters() {
        when(inputValidator.validateSearchQuery(null)).thenReturn(null);
        when(inputValidator.validateCategory(null)).thenReturn(null);
        when(itemRepository.findAll()).thenReturn(Flux.fromIterable(testItems));

        Flux<Item> result = searchItemsService.execute(null, null);

        StepVerifier.create(result)
                .expectNextCount(2)
                .verifyComplete();
        
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should search by query when provided")
    void shouldSearchByQuery() {
        String query = "laptop";
        when(inputValidator.validateSearchQuery(query)).thenReturn(query);
        when(inputValidator.validateCategory(null)).thenReturn(null);
        when(itemRepository.findByQuery(query)).thenReturn(Flux.just(testItems.get(0)));

        Flux<Item> result = searchItemsService.execute(query, null);

        StepVerifier.create(result)
                .expectNextMatches(item -> item.getTitle().equals("Laptop Dell"))
                .verifyComplete();
        
        verify(itemRepository, times(1)).findByQuery(query);
    }

    @Test
    @DisplayName("Should search by category when provided")
    void shouldSearchByCategory() {
        String category = "CAT001";
        when(inputValidator.validateSearchQuery(null)).thenReturn(null);
        when(inputValidator.validateCategory(category)).thenReturn(category);
        when(itemRepository.findByCategory(category)).thenReturn(Flux.just(testItems.get(0)));

        Flux<Item> result = searchItemsService.execute(null, category);

        StepVerifier.create(result)
                .expectNextMatches(item -> item.getCategory().getId().equals("CAT001"))
                .verifyComplete();
        
        verify(itemRepository, times(1)).findByCategory(category);
    }

    @Test
    @DisplayName("Should search by query and category when both provided")
    void shouldSearchByQueryAndCategory() {
        String query = "laptop";
        String category = "CAT001";
        when(inputValidator.validateSearchQuery(query)).thenReturn(query);
        when(inputValidator.validateCategory(category)).thenReturn(category);
        when(itemRepository.findByQueryAndCategory(query, category))
                .thenReturn(Flux.just(testItems.get(0)));

        Flux<Item> result = searchItemsService.execute(query, category);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
        
        verify(itemRepository, times(1)).findByQueryAndCategory(query, category);
    }

    @Test
    @DisplayName("Should return empty Flux when no results")
    void shouldReturnEmptyFluxWhenNoResults() {
        when(inputValidator.validateSearchQuery("nonexistent")).thenReturn("nonexistent");
        when(inputValidator.validateCategory(null)).thenReturn(null);
        when(itemRepository.findByQuery("nonexistent")).thenReturn(Flux.empty());

        Flux<Item> result = searchItemsService.execute("nonexistent", null);

        StepVerifier.create(result)
                .verifyComplete();
        
        verify(itemRepository, times(1)).findByQuery("nonexistent");
    }

    @Test
    @DisplayName("Should return paginated results correctly")
    void shouldReturnPaginatedResults() {
        int page = 0;
        int size = 3;
        when(inputValidator.validateSearchQuery(null)).thenReturn(null);
        when(inputValidator.validateCategory(null)).thenReturn(null);
        doNothing().when(inputValidator).validatePagination(page, size);
        when(itemRepository.findAll()).thenReturn(Flux.fromIterable(testItems));

        Mono<PaginatedResult<Item>> result = searchItemsService.executeWithPagination(null, null, page, size);

        StepVerifier.create(result)
                .assertNext(paginatedResult -> {
                    assertThat(paginatedResult.content()).hasSize(2);
                    assertThat(paginatedResult.totalElements()).isEqualTo(2);
                    assertThat(paginatedResult.page()).isEqualTo(0);
                    assertThat(paginatedResult.size()).isEqualTo(3);
                    assertThat(paginatedResult.hasNext()).isFalse();
                    assertThat(paginatedResult.hasPrevious()).isFalse();
                })
                .verifyComplete();

        verify(inputValidator, times(1)).validatePagination(page, size);
    }

    @Test
    @DisplayName("Should return empty page when exceeding range")
    void shouldReturnEmptyPageWhenExceedingRange() {
        int page = 10;
        int size = 3;
        when(inputValidator.validateSearchQuery(null)).thenReturn(null);
        when(inputValidator.validateCategory(null)).thenReturn(null);
        doNothing().when(inputValidator).validatePagination(page, size);
        when(itemRepository.findAll()).thenReturn(Flux.fromIterable(testItems));

        Mono<PaginatedResult<Item>> result = searchItemsService.executeWithPagination(null, null, page, size);

        StepVerifier.create(result)
                .assertNext(paginatedResult -> {
                    assertThat(paginatedResult.content()).isEmpty();
                    assertThat(paginatedResult.totalElements()).isEqualTo(2);
                    assertThat(paginatedResult.page()).isEqualTo(10);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should validate pagination parameters before searching")
    void shouldValidatePaginationParameters() {
        int page = -1;
        int size = 10;
        doThrow(new ValidationException("Page number cannot be negative"))
                .when(inputValidator).validatePagination(page, size);

        try {
            searchItemsService.executeWithPagination(null, null, page, size);
            assertThat(false).as("Should have thrown ValidationException").isTrue();
        } catch (ValidationException e) {
            assertThat(e.getMessage()).isEqualTo("Page number cannot be negative");
        }

        verify(inputValidator, times(1)).validatePagination(page, size);
        verify(itemRepository, never()).findAll();
    }
}
