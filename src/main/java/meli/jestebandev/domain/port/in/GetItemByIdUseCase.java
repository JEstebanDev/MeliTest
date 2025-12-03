package meli.jestebandev.domain.port.in;

import meli.jestebandev.domain.model.Item;
import reactor.core.publisher.Mono;

public interface GetItemByIdUseCase {
    Mono<Item> execute(String id);
}

