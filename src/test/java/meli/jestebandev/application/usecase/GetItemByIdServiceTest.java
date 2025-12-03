package meli.jestebandev.application.usecase;

import meli.jestebandev.domain.exception.ItemNotFoundException;
import meli.jestebandev.domain.model.Category;
import meli.jestebandev.domain.model.Item;
import meli.jestebandev.domain.model.ItemCondition;
import meli.jestebandev.domain.model.Seller;
import meli.jestebandev.domain.port.out.InputValidator;
import meli.jestebandev.domain.port.out.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetItemByIdService Unit Tests")
class GetItemByIdServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private InputValidator inputValidator;

    @InjectMocks
    private GetItemByIdService getItemByIdService;

    private Item testItem;

    @BeforeEach
    void setUp() {
        testItem = Item.builder()
                .id("MLU123456789")
                .title("Laptop Test")
                .price(BigDecimal.valueOf(1299.99))
                .description("Test description")
                .image("http://example.com/image.jpg")
                .stock(10)
                .condition(ItemCondition.NEW)
                .category(Category.builder()
                        .id("CAT001")
                        .name("Computación")
                        .build())
                .seller(Seller.builder()
                        .id("SELLER001")
                        .name("Test Seller")
                        .reputation(4.5)
                        .build())
                .build();
    }

    @Test
    @DisplayName("Should return item when it exists")
    void shouldReturnItemWhenExists() {
        when(inputValidator.validateItemId("MLU123456789")).thenReturn("MLU123456789");
        when(itemRepository.findById("MLU123456789")).thenReturn(Mono.just(testItem));

        Mono<Item> result = getItemByIdService.execute("MLU123456789");

        StepVerifier.create(result)
                .expectNextMatches(item -> 
                        item.getId().equals("MLU123456789") &&
                        item.getTitle().equals("Laptop Test") &&
                        item.getPrice().equals(BigDecimal.valueOf(1299.99))
                )
                .verifyComplete();
        
        verify(inputValidator, times(1)).validateItemId("MLU123456789");
        verify(itemRepository, times(1)).findById("MLU123456789");
    }

    @Test
    @DisplayName("Should throw ItemNotFoundException when item does not exist")
    void shouldThrowItemNotFoundExceptionWhenNotExists() {
        when(inputValidator.validateItemId(anyString())).thenReturn("INVALID_ID");
        when(itemRepository.findById(anyString())).thenReturn(Mono.empty());

        Mono<Item> result = getItemByIdService.execute("INVALID_ID");

        StepVerifier.create(result)
                .expectError(ItemNotFoundException.class)
                .verify();
        
        verify(inputValidator, times(1)).validateItemId("INVALID_ID");
        verify(itemRepository, times(1)).findById("INVALID_ID");
    }

    @Test
    @DisplayName("Should return item with all complete data")
    void shouldReturnItemWithCompleteData() {
        when(inputValidator.validateItemId("MLU123456789")).thenReturn("MLU123456789");
        when(itemRepository.findById("MLU123456789")).thenReturn(Mono.just(testItem));

        Mono<Item> result = getItemByIdService.execute("MLU123456789");

        StepVerifier.create(result)
                .expectNextMatches(item ->
                        item.getCondition() == ItemCondition.NEW &&
                        item.getCategory() != null &&
                        item.getCategory().getId().equals("CAT001") &&
                        item.getSeller() != null &&
                        item.getSeller().getId().equals("SELLER001") &&
                        item.getSeller().getReputation() == 4.5
                )
                .verifyComplete();
        
        verify(inputValidator, times(1)).validateItemId("MLU123456789");
    }
}

