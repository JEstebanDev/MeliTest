package meli.jestebandev.application.usecase;

import lombok.RequiredArgsConstructor;
import meli.jestebandev.domain.exception.ItemNotFoundException;
import meli.jestebandev.domain.model.Item;
import meli.jestebandev.domain.port.in.GetItemByIdUseCase;
import meli.jestebandev.domain.port.out.InputValidator;
import meli.jestebandev.domain.port.out.ItemRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GetItemByIdService implements GetItemByIdUseCase {

    private final ItemRepository itemRepository;
    private final InputValidator inputValidator;

    @Override
    public Mono<Item> execute(String id) {
        String validatedId = inputValidator.validateItemId(id);
        
        return itemRepository.findById(validatedId)
                .switchIfEmpty(Mono.error(new ItemNotFoundException(validatedId)));
    }
}

