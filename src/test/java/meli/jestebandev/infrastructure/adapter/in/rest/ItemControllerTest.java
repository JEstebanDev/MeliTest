package meli.jestebandev.infrastructure.adapter.in.rest;

import meli.jestebandev.domain.model.Category;
import meli.jestebandev.domain.model.Item;
import meli.jestebandev.domain.model.ItemCondition;
import meli.jestebandev.domain.model.PaginatedResult;
import meli.jestebandev.domain.model.Seller;
import meli.jestebandev.domain.port.in.GetItemByIdUseCase;
import meli.jestebandev.domain.port.in.SearchItemsUseCase;
import meli.jestebandev.infrastructure.adapter.in.rest.dto.ItemResponse;
import meli.jestebandev.infrastructure.adapter.in.rest.dto.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemController Unit Tests")
class ItemControllerTest {

    @Mock
    private GetItemByIdUseCase getItemByIdUseCase;

    @Mock
    private SearchItemsUseCase searchItemsUseCase;

    @InjectMocks
    private ItemController itemController;

    private Item testItem;
    private Category testCategory;
    private Seller testSeller;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .id("CAT001")
                .name("Computación")
                .build();

        testSeller = Seller.builder()
                .id("SELLER001")
                .name("Test Seller")
                .reputation(4.5)
                .build();

        testItem = Item.builder()
                .id("MLU123456789")
                .title("Laptop Test")
                .price(BigDecimal.valueOf(1299.99))
                .description("Test description")
                .image("http://example.com/image.jpg")
                .stock(10)
                .condition(ItemCondition.NEW)
                .category(testCategory)
                .seller(testSeller)
                .build();
    }

    @Test
    @DisplayName("Should return item correctly mapped to ItemResponse")
    void shouldReturnItemCorrectlyMapped() {
        when(getItemByIdUseCase.execute("MLU123456789")).thenReturn(Mono.just(testItem));

        Mono<ResponseEntity<ItemResponse>> result = itemController.getItemById("MLU123456789");

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCodeValue()).isEqualTo(200);
                    assertThat(response.getBody()).isNotNull();
                    
                    ItemResponse itemResponse = response.getBody();
                    assertThat(itemResponse.getId()).isEqualTo("MLU123456789");
                    assertThat(itemResponse.getTitle()).isEqualTo("Laptop Test");
                    assertThat(itemResponse.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(1299.99));
                    assertThat(itemResponse.getCondition()).isEqualTo("NEW");
                    assertThat(itemResponse.getCategory()).isNotNull();
                    assertThat(itemResponse.getCategory().getId()).isEqualTo("CAT001");
                    assertThat(itemResponse.getSeller()).isNotNull();
                    assertThat(itemResponse.getSeller().getId()).isEqualTo("SELLER001");
                })
                .verifyComplete();

        verify(getItemByIdUseCase, times(1)).execute("MLU123456789");
    }

    @Test
    @DisplayName("Should handle item without optional fields")
    void shouldHandleItemWithoutOptionalFields() {
        Item itemWithoutOptionals = testItem.toBuilder()
                .category(null)
                .seller(null)
                .build();
        when(getItemByIdUseCase.execute("MLU123456789")).thenReturn(Mono.just(itemWithoutOptionals));

        Mono<ResponseEntity<ItemResponse>> result = itemController.getItemById("MLU123456789");

        StepVerifier.create(result)
                .assertNext(response -> {
                    ItemResponse itemResponse = response.getBody();
                    assertThat(itemResponse.getCategory()).isNull();
                    assertThat(itemResponse.getSeller()).isNull();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return paginated search results correctly")
    void shouldReturnPaginatedSearchResults() {
        List<Item> testItems = Arrays.asList(
                Item.builder()
                        .id("MLU001")
                        .title("Laptop Dell")
                        .price(BigDecimal.valueOf(999.99))
                        .condition(ItemCondition.NEW)
                        .category(testCategory)
                        .seller(testSeller)
                        .build(),
                Item.builder()
                        .id("MLU002")
                        .title("iPhone 15")
                        .price(BigDecimal.valueOf(1199.99))
                        .condition(ItemCondition.NEW)
                        .category(testCategory)
                        .seller(testSeller)
                        .build()
        );

        PaginatedResult<Item> paginatedResult = new PaginatedResult<>(testItems, 20L, 0, 10);
        when(searchItemsUseCase.executeWithPagination(eq(null), eq(null), eq(0), eq(10)))
                .thenReturn(Mono.just(paginatedResult));

        Mono<ResponseEntity<PageResponse<ItemResponse>>> result = 
                itemController.searchItems(null, null, 0, 10);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCodeValue()).isEqualTo(200);
                    assertThat(response.getBody()).isNotNull();
                    
                    PageResponse<ItemResponse> pageResponse = response.getBody();
                    assertThat(pageResponse.getContent()).hasSize(2);
                    assertThat(pageResponse.getTotalElements()).isEqualTo(20);
                    assertThat(pageResponse.getPage()).isEqualTo(0);
                    assertThat(pageResponse.getSize()).isEqualTo(10);
                    assertThat(pageResponse.isHasNext()).isTrue();
                    assertThat(pageResponse.isHasPrevious()).isFalse();
                })
                .verifyComplete();

        verify(searchItemsUseCase, times(1)).executeWithPagination(null, null, 0, 10);
    }

    @Test
    @DisplayName("Should search with query and category filters")
    void shouldSearchWithFilters() {
        String query = "laptop";
        String category = "CAT001";
        List<Item> testItems = Collections.singletonList(testItem);
        PaginatedResult<Item> paginatedResult = new PaginatedResult<>(testItems, 1L, 0, 10);
        
        when(searchItemsUseCase.executeWithPagination(eq(query), eq(category), eq(0), eq(10)))
                .thenReturn(Mono.just(paginatedResult));

        Mono<ResponseEntity<PageResponse<ItemResponse>>> result = 
                itemController.searchItems(query, category, 0, 10);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCodeValue()).isEqualTo(200);
                    PageResponse<ItemResponse> pageResponse = response.getBody();
                    assertThat(pageResponse.getContent()).hasSize(1);
                })
                .verifyComplete();

        verify(searchItemsUseCase, times(1)).executeWithPagination(query, category, 0, 10);
    }

    @Test
    @DisplayName("Should correctly map items to ItemResponse")
    void shouldCorrectlyMapItemsToItemResponse() {
        List<Item> testItems = Arrays.asList(testItem);
        PaginatedResult<Item> paginatedResult = new PaginatedResult<>(testItems, 1L, 0, 10);
        
        when(searchItemsUseCase.executeWithPagination(anyString(), eq(null), anyInt(), anyInt()))
                .thenReturn(Mono.just(paginatedResult));

        Mono<ResponseEntity<PageResponse<ItemResponse>>> result = 
                itemController.searchItems("laptop", null, 0, 10);

        StepVerifier.create(result)
                .assertNext(response -> {
                    PageResponse<ItemResponse> pageResponse = response.getBody();
                    List<ItemResponse> items = pageResponse.getContent();
                    
                    assertThat(items.get(0).getId()).isEqualTo("MLU123456789");
                    assertThat(items.get(0).getTitle()).isEqualTo("Laptop Test");
                    assertThat(items.get(0).getPrice()).isEqualByComparingTo(BigDecimal.valueOf(1299.99));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle empty search results")
    void shouldHandleEmptySearchResults() {
        PaginatedResult<Item> emptyResult = new PaginatedResult<>(
                Collections.emptyList(), 0L, 0, 10
        );
        when(searchItemsUseCase.executeWithPagination(anyString(), eq(null), anyInt(), anyInt()))
                .thenReturn(Mono.just(emptyResult));

        Mono<ResponseEntity<PageResponse<ItemResponse>>> result = 
                itemController.searchItems("nonexistent", null, 0, 10);

        StepVerifier.create(result)
                .assertNext(response -> {
                    PageResponse<ItemResponse> pageResponse = response.getBody();
                    assertThat(pageResponse.getContent()).isEmpty();
                    assertThat(pageResponse.getTotalElements()).isEqualTo(0);
                    assertThat(pageResponse.getTotalPages()).isEqualTo(0);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should correctly map pagination metadata")
    void shouldCorrectlyMapPaginationMetadata() {
        List<Item> testItems = Arrays.asList(testItem);
        PaginatedResult<Item> middlePageResult = new PaginatedResult<>(testItems, 100L, 5, 10);
        
        when(searchItemsUseCase.executeWithPagination(eq(null), eq(null), eq(5), eq(10)))
                .thenReturn(Mono.just(middlePageResult));

        Mono<ResponseEntity<PageResponse<ItemResponse>>> result = 
                itemController.searchItems(null, null, 5, 10);

        StepVerifier.create(result)
                .assertNext(response -> {
                    PageResponse<ItemResponse> pageResponse = response.getBody();
                    assertThat(pageResponse.getPage()).isEqualTo(5);
                    assertThat(pageResponse.getTotalPages()).isEqualTo(10);
                    assertThat(pageResponse.isHasNext()).isTrue();
                    assertThat(pageResponse.isHasPrevious()).isTrue();
                    assertThat(pageResponse.isFirst()).isFalse();
                    assertThat(pageResponse.isLast()).isFalse();
                })
                .verifyComplete();
    }
}
