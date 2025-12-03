package meli.jestebandev.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Seller {
    String id;
    String name;
    Double reputation;
}