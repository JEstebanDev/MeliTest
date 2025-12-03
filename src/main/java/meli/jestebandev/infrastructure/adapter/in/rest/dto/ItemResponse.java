package meli.jestebandev.infrastructure.adapter.in.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import meli.jestebandev.domain.model.Item;
import meli.jestebandev.domain.model.ItemCondition;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {
    private String id;
    private String title;
    private BigDecimal price;
    private String description;
    private String image;
    private Integer stock;
    private String condition;
    private CategoryResponse category;
    private SellerResponse seller;

    public static ItemResponse fromDomain(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .price(item.getPrice())
                .description(item.getDescription())
                .image(item.getImage())
                .stock(item.getStock())
                .condition(item.getCondition() != null ? item.getCondition().name() : null)
                .category(item.getCategory() != null ? CategoryResponse.fromDomain(item.getCategory()) : null)
                .seller(item.getSeller() != null ? SellerResponse.fromDomain(item.getSeller()) : null)
                .build();
    }
}

