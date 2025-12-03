package meli.jestebandev.infrastructure.adapter.in.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import meli.jestebandev.domain.model.Seller;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerResponse {
    private String id;
    private String name;
    private Double reputation;

    public static SellerResponse fromDomain(Seller seller) {
        return SellerResponse.builder()
                .id(seller.getId())
                .name(seller.getName())
                .reputation(seller.getReputation())
                .build();
    }
}

