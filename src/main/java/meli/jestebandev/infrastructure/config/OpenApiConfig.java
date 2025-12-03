package meli.jestebandev.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MercadoLibre Items API")
                        .version("1.0.0")
                        .description("""
                                REST API for product management inspired by MercadoLibre.
                                
                                This API provides endpoints to:
                                - Retrieve product details by ID
                                - Search and filter products by text and category
                                - Paginated results for efficient data handling
                                
                                **Architecture:** Hexagonal Architecture (Ports and Adapters)
                                **Tech Stack:** Spring Boot 3.2 + Java 21 + WebFlux (Reactive)
                                **Design Principles:** SOLID, Clean Architecture, Immutable Entities
                                
                                **Note:** All endpoints are reactive and non-blocking, providing high scalability and performance.
                                """)
                        .contact(new Contact()
                                .name("jestebandev")
                                .url("https://github.com/jestebandev")
                                .email("castanoesteban9@gmail.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server (Netty)")
                ));
    }
}

