package meli.jestebandev.application.usecase;

import lombok.RequiredArgsConstructor;
import meli.jestebandev.domain.model.Item;
import meli.jestebandev.domain.model.PaginatedResult;
import meli.jestebandev.domain.port.in.SearchItemsUseCase;
import meli.jestebandev.domain.port.out.InputValidator;
import meli.jestebandev.domain.port.out.ItemRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchItemsService implements SearchItemsUseCase {

    private final ItemRepository itemRepository;
    private final InputValidator inputValidator;

    @Override
    public Flux<Item> execute(String query, String categoryId) {
        String validatedQuery = inputValidator.validateSearchQuery(query);
        String validatedCategory = inputValidator.validateCategory(categoryId);
        return getFilteredItems(validatedQuery, validatedCategory);
    }

    @Override
    public Mono<PaginatedResult<Item>> executeWithPagination(String query, String categoryId, int page, int size) {
        inputValidator.validatePagination(page, size);

        String validatedQuery = inputValidator.validateSearchQuery(query);
        String validatedCategory = inputValidator.validateCategory(categoryId);
        
        Flux<Item> itemsFlux = getFilteredItems(validatedQuery, validatedCategory);

        Mono<Long> totalCount = itemsFlux.count();

        Flux<Item> paginatedItems = getFilteredItems(validatedQuery, validatedCategory)
                .skip((long) page * size)
                .take(size);

        return Mono.zip(paginatedItems.collectList(), totalCount)
                .map(tuple -> {
                    List<Item> content = tuple.getT1();
                    long totalElements = tuple.getT2();
                    return new PaginatedResult<>(content, totalElements, page, size);
                });
    }

    private Flux<Item> getFilteredItems(String query, String categoryId) {
        if (query != null && !query.isBlank() && categoryId != null && !categoryId.isBlank()) {
            return itemRepository.findByQueryAndCategory(query, categoryId);
        }
        
        if (query != null && !query.isBlank()) {
            return itemRepository.findByQuery(query);
        }
        
        if (categoryId != null && !categoryId.isBlank()) {
            return itemRepository.findByCategory(categoryId);
        }
        
        return itemRepository.findAll();
    }
}

