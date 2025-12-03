package meli.jestebandev.domain.port.out;

import meli.jestebandev.domain.model.Item;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemRepository {

    Mono<Item> findById(String id);

    Flux<Item> findAll();

    Flux<Item> findByQuery(String query);

    Flux<Item> findByCategory(String categoryId);

    Flux<Item> findByQueryAndCategory(String query, String categoryId);
}

