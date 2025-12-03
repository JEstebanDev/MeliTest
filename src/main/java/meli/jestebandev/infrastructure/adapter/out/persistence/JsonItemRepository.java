package meli.jestebandev.infrastructure.adapter.out.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meli.jestebandev.domain.model.Item;
import meli.jestebandev.domain.port.out.ItemRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JsonItemRepository implements ItemRepository {

    private final ObjectMapper objectMapper;
    private Mono<List<Item>> itemsCacheMono;

    private Mono<List<Item>> loadItems() {
        if (itemsCacheMono == null) {
            itemsCacheMono = Mono.fromCallable(() -> {
                try {
                    ClassPathResource resource = new ClassPathResource("data/items.json");
                    List<Item> items = objectMapper.readValue(
                            resource.getInputStream(),
                            new TypeReference<List<Item>>() {}
                    );
                    log.info("Loaded {} items from JSON file", items.size());
                    return items;
                } catch (IOException e) {
                    log.error("Error loading items from JSON", e);
                    throw new RuntimeException("Error loading items", e);
                }
            })
            .subscribeOn(Schedulers.boundedElastic())
            .cache();
        }
        return itemsCacheMono;
    }

    @Override
    public Mono<Item> findById(String id) {
        return loadItems()
                .flatMapMany(Flux::fromIterable)
                .filter(item -> item.getId().equals(id))
                .next();
    }

    @Override
    public Flux<Item> findAll() {
        return loadItems()
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Flux<Item> findByQuery(String query) {
        String lowerQuery = query.toLowerCase();
        return loadItems()
                .flatMapMany(Flux::fromIterable)
                .filter(item -> matchesQuery(item, lowerQuery));
    }

    @Override
    public Flux<Item> findByCategory(String categoryId) {
        return loadItems()
                .flatMapMany(Flux::fromIterable)
                .filter(item -> item.getCategory() != null 
                        && item.getCategory().getId().equals(categoryId));
    }

    @Override
    public Flux<Item> findByQueryAndCategory(String query, String categoryId) {
        String lowerQuery = query.toLowerCase();
        return loadItems()
                .flatMapMany(Flux::fromIterable)
                .filter(item -> item.getCategory() != null 
                        && item.getCategory().getId().equals(categoryId))
                .filter(item -> matchesQuery(item, lowerQuery));
    }

    private boolean matchesQuery(Item item, String lowerQuery) {
        return (item.getTitle() != null && item.getTitle().toLowerCase().contains(lowerQuery))
                || (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerQuery));
    }
}

