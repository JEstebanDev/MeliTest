package meli.jestebandev.infrastructure.adapter.out.persistence;

import meli.jestebandev.domain.model.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@DisplayName("JsonItemRepository Integration Tests")
class JsonItemRepositoryTest {

    @Autowired
    private JsonItemRepository jsonItemRepository;

    @Test
    @DisplayName("Should load items from JSON file")
    void shouldLoadItemsFromJson() {
        Flux<Item> items = jsonItemRepository.findAll();

        StepVerifier.create(items)
                .expectNextCount(13)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find item by ID")
    void shouldFindItemById() {
        Mono<Item> item = jsonItemRepository.findById("MLU123456789");

        StepVerifier.create(item)
                .expectNextMatches(i -> 
                        i.getId().equals("MLU123456789") && 
                        i.getTitle() != null
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty Mono when item does not exist")
    void shouldReturnEmptyMonoWhenItemNotExists() {
        Mono<Item> item = jsonItemRepository.findById("INVALID_ID");

        StepVerifier.create(item)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find items by query in title")
    void shouldFindItemsByQueryInTitle() {
        Flux<Item> items = jsonItemRepository.findByQuery("laptop");

        StepVerifier.create(items)
                .expectNextMatches(item -> 
                        item.getTitle().toLowerCase().contains("laptop")
                )
                .expectNextMatches(item -> 
                        item.getTitle().toLowerCase().contains("laptop")
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find items by query in description")
    void shouldFindItemsByQueryInDescription() {
        Flux<Item> items = jsonItemRepository.findByQuery("processor");

        StepVerifier.create(items)
                .expectNextMatches(item -> 
                        item.getDescription() != null &&
                        item.getDescription().toLowerCase().contains("processor")
                )
                .thenConsumeWhile(item -> true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find items by category")
    void shouldFindItemsByCategory() {
        Flux<Item> items = jsonItemRepository.findByCategory("MLA1648");

        StepVerifier.create(items)
                .expectNextMatches(item -> 
                        item.getCategory() != null && 
                        item.getCategory().getId().equals("MLA1648")
                )
                .thenConsumeWhile(item -> 
                        item.getCategory() != null && 
                        item.getCategory().getId().equals("MLA1648")
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find items by query and category")
    void shouldFindItemsByQueryAndCategory() {
        Flux<Item> items = jsonItemRepository.findByQueryAndCategory("laptop", "MLA1648");

        StepVerifier.create(items)
                .expectNextMatches(item ->
                        (item.getTitle().toLowerCase().contains("laptop") ||
                         item.getDescription().toLowerCase().contains("laptop")) &&
                        item.getCategory().getId().equals("MLA1648")
                )
                .thenConsumeWhile(item -> true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty Flux when search has no results")
    void shouldReturnEmptyFluxWhenSearchHasNoResults() {
        Flux<Item> items = jsonItemRepository.findByQuery("xyznonexistent123");

        StepVerifier.create(items)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should be case-insensitive in search")
    void shouldBeCaseInsensitiveInSearch() {
        Flux<Item> itemsLower = jsonItemRepository.findByQuery("laptop");
        Flux<Item> itemsUpper = jsonItemRepository.findByQuery("LAPTOP");
        Flux<Item> itemsMixed = jsonItemRepository.findByQuery("LaPtOp");

        StepVerifier.create(itemsLower.count())
                .expectNextMatches(count -> count > 0)
                .verifyComplete();

        StepVerifier.create(itemsUpper.count())
                .expectNextMatches(count -> count > 0)
                .verifyComplete();

        StepVerifier.create(itemsMixed.count())
                .expectNextMatches(count -> count > 0)
                .verifyComplete();
    }
}

