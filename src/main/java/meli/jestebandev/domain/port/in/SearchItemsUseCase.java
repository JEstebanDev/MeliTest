package meli.jestebandev.domain.port.in;

import meli.jestebandev.domain.model.Item;
import meli.jestebandev.domain.model.PaginatedResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SearchItemsUseCase {

    Flux<Item> execute(String query, String categoryId);

    Mono<PaginatedResult<Item>> executeWithPagination(String query, String categoryId, int page, int size);
}

