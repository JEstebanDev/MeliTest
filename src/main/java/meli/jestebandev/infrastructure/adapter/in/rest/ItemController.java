package meli.jestebandev.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import meli.jestebandev.domain.port.in.GetItemByIdUseCase;
import meli.jestebandev.domain.port.in.SearchItemsUseCase;
import meli.jestebandev.infrastructure.adapter.in.rest.dto.ItemResponse;
import meli.jestebandev.infrastructure.adapter.in.rest.dto.PageResponse;
import meli.jestebandev.infrastructure.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Tag(name = "Items", description = "Endpoints for product management - Retrieve, search, and filter products")
public class ItemController {

    private final GetItemByIdUseCase getItemByIdUseCase;
    private final SearchItemsUseCase searchItemsUseCase;

    @GetMapping("/{id}")
    @Operation(
            summary = "Get Product by ID",
            description = """
                    Retrieves complete information of a specific product by its unique identifier.
                    
                    Returns detailed information including:
                    - Product details (title, price, description, image, stock, condition)
                    - Category information
                    - Seller information and reputation
                    
                    **Reactive:** This endpoint is non-blocking and returns a Mono<ResponseEntity>.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product found successfully",
                    content = @Content(schema = @Schema(implementation = ItemResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found - The specified product ID does not exist",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public Mono<ResponseEntity<ItemResponse>> getItemById(
            @Parameter(
                    description = "Unique product identifier (MercadoLibre format)",
                    example = "MLU123456789",
                    required = true
            )
            @PathVariable String id
    ) {
        return getItemByIdUseCase.execute(id)
                .map(ItemResponse::fromDomain)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    @Operation(
            summary = "Search and Filter Products",
            description = """
                    Search and filter products with flexible criteria and pagination support.
                    
                    **Search Capabilities:**
                    - Text search in product titles and descriptions (case-insensitive)
                    - Filter by category ID
                    - Combine multiple filters
                    - Paginated results for efficient data handling
                    
                    **Default Behavior:** If no filters are provided, returns all products paginated.
                    
                    **Pagination:**
                    - Pages are 0-indexed (first page = 0)
                    - Default page size: 10 items
                    - Maximum page size: 100 items
                    
                    **Reactive:** This endpoint is non-blocking and returns a Mono<ResponseEntity>.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Search completed successfully - Returns paginated results even if empty",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid parameters (e.g., negative page number, invalid page size)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public Mono<ResponseEntity<PageResponse<ItemResponse>>> searchItems(
            @Parameter(
                    description = "Search text to match in product title or description",
                    example = "laptop"
            )
            @RequestParam(required = false) String q,
            @Parameter(
                    description = "Category ID to filter products (MercadoLibre category format)",
                    example = "MLA1648"
            )
            @RequestParam(required = false) String category,
            @Parameter(
                    description = "Page number (0-based index). First page is 0.",
                    example = "0"
            )
            @RequestParam(defaultValue = "0") int page,
            @Parameter(
                    description = "Number of items per page (max: 100)",
                    example = "10"
            )
            @RequestParam(defaultValue = "10") int size
    ) {
        return searchItemsUseCase.executeWithPagination(q, category, page, size)
                .map(result -> {
                    List<ItemResponse> itemResponses = result.content().stream()
                            .map(ItemResponse::fromDomain)
                            .toList();
                    
                    PageResponse<ItemResponse> pageResponse = PageResponse.<ItemResponse>builder()
                            .content(itemResponses)
                            .page(result.page())
                            .size(result.size())
                            .totalElements(result.totalElements())
                            .totalPages(result.totalPages())
                            .hasNext(result.hasNext())
                            .hasPrevious(result.hasPrevious())
                            .isFirst(result.isFirst())
                            .isLast(result.isLast())
                            .build();
                    
                    return ResponseEntity.ok(pageResponse);
                });
    }
}

