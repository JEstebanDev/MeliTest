package meli.jestebandev.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Item {
    String id;
    String title;
    BigDecimal price;
    String description;
    String image;
    Integer stock;
    ItemCondition condition;
    Category category;
    Seller seller;
}