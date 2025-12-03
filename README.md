# MercadoLibre Items API

REST API for product management inspired by MercadoLibre. Implementation with **Hexagonal Architecture**, **SOLID principles**, and **Reactive Programming**.

For this test, I started by writing some ideas in my notebook about what the project should do and any special things to keep in mind. From there, I shaped what you see here.
I hope you enjoy checking it out — it was definitely powered by a good Colombian coffee, haha.

---

## 🚀 Quick Start

### Requirements
- Java 21+
- Gradle 8.x (wrapper included)

### Run the Application
```bash
# Windows
gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

The API will be available at: `http://localhost:8080`

**Interactive Documentation:** `http://localhost:8080/swagger-ui.html`

---

## 📡 Main Endpoints

### 1. Get Product by ID
```http
GET /api/items/{id}
```

**Example:**
```bash
curl http://localhost:8080/api/items/MLU123456789
```

### 2. Search/List Products
```http
GET /api/items?q={query}&category={categoryId}&page={page}&size={size}
```

**Parameters:**
- `q` (optional): Text search in title/description
- `category` (optional): Filter by category
- `page` (optional, default=0): Page number
- `size` (optional, default=10, max=100): Items per page

**Examples:**
```bash
# List all products
curl http://localhost:8080/api/items

# Search by text
curl "http://localhost:8080/api/items?q=laptop"

# Filter by category
curl "http://localhost:8080/api/items?category=MLA1648"

# Combined search with pagination
curl "http://localhost:8080/api/items?q=laptop&category=MLA1648&page=0&size=5"
```

---

## 🏗️ Architecture

### Design: Hexagonal Architecture (Ports and Adapters)

```
┌─────────────────────────────────────────────────────┐
│         Infrastructure Layer (Adapters)             │
│                                                     │
│  REST Controller  │  JSON Repository  │  Validator  │
└──────────┬────────┴───────────┬───────┴─────────────┘
           │ implements         │ implements
           ↓                    ↓
┌─────────────────────────────────────────────────────┐
│              Domain Layer (Core Business)           │
│                                                     │
│  Ports (Interfaces):                                │
│  • GetItemByIdUseCase    • ItemRepository           │
│  • SearchItemsUseCase    • InputValidator           │
│                                                     │
│  Models: Item, Category, Seller, PaginatedResult    │
└──────────┬──────────────────────────────────────────┘
           ↑ depends on
           │
┌──────────┴──────────────────────────────────────────┐
│          Application Layer (Use Cases)              │
│                                                     │
│  • GetItemByIdService    • SearchItemsService       │
└─────────────────────────────────────────────────────┘
```

**Dependency Flow:** `Infrastructure → Domain ← Application`

---

## 🧪 Testing

### Run Tests
```bash
./gradlew test
```

**Test Report:** `build/reports/tests/test/index.html`

---

**Developed by:** jestebandev  
**Technology:** Spring Boot 3.2 + Java 21 + WebFlux  
**Architecture:** Hexagonal + Clean Architecture + SOLID  
