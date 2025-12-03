package meli.jestebandev.infrastructure.adapter.in.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName("ItemController Integration Tests")
class ItemControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("GET /api/items/{id} - Should return existing item with complete structure")
    void shouldReturnExistingItem() {
        webTestClient.get()
                .uri("/api/items/MLU123456789")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo("MLU123456789")
                .jsonPath("$.title").exists()
                .jsonPath("$.price").exists()
                .jsonPath("$.description").exists()
                .jsonPath("$.category.id").exists()
                .jsonPath("$.category.name").exists()
                .jsonPath("$.seller.id").exists()
                .jsonPath("$.seller.name").exists()
                .jsonPath("$.seller.reputation").exists();
    }

    @Test
    @DisplayName("GET /api/items/{id} - Should return 404 when item does not exist")
    void shouldReturn404WhenItemNotExists() {
        webTestClient.get()
                .uri("/api/items/MLU999999999")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found")
                .jsonPath("$.message").exists()
                .jsonPath("$.timestamp").exists();
    }

    @Test
    @DisplayName("GET /api/items - Should return all items with pagination")
    void shouldReturnAllItemsWithPagination() {
        webTestClient.get()
                .uri("/api/items")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(10)
                .jsonPath("$.page").isEqualTo(0)
                .jsonPath("$.size").isEqualTo(10)
                .jsonPath("$.totalElements").isEqualTo(13)
                .jsonPath("$.totalPages").isEqualTo(2)
                .jsonPath("$.hasNext").isEqualTo(true)
                .jsonPath("$.hasPrevious").isEqualTo(false);
    }

    @Test
    @DisplayName("GET /api/items?q=laptop - Should search by query")
    void shouldSearchByQuery() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/items")
                        .queryParam("q", "laptop")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content").isNotEmpty()
                .jsonPath("$.totalElements").exists();
    }

    @Test
    @DisplayName("GET /api/items?category=MLA1648 - Should search by category")
    void shouldSearchByCategory() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/items")
                        .queryParam("category", "MLA1648")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content").isNotEmpty();
    }

    @Test
    @DisplayName("GET /api/items?q=nonexistent - Should return empty page when no results")
    void shouldReturnEmptyPageWhenNoResults() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/items")
                        .queryParam("q", "xyznonexistent123")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(0)
                .jsonPath("$.totalElements").isEqualTo(0)
                .jsonPath("$.totalPages").isEqualTo(0);
    }

    @Test
    @DisplayName("GET /api/items?page=0&size=5 - Should paginate with custom size")
    void shouldPaginateWithCustomSize() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/items")
                        .queryParam("page", 0)
                        .queryParam("size", 5)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo(5)
                .jsonPath("$.page").isEqualTo(0)
                .jsonPath("$.size").isEqualTo(5)
                .jsonPath("$.totalElements").isEqualTo(13)
                .jsonPath("$.totalPages").isEqualTo(3)
                .jsonPath("$.hasNext").isEqualTo(true)
                .jsonPath("$.hasPrevious").isEqualTo(false);
    }
}
