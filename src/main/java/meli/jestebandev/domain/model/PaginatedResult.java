package meli.jestebandev.domain.model;

import java.util.List;

public record PaginatedResult<T>(
        List<T> content,
        long totalElements,
        int page,
        int size
) {
    public int totalPages() {
        return size == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
    }

    public boolean hasNext() {
        return page < totalPages() - 1;
    }

    public boolean hasPrevious() {
        return page > 0;
    }

    public boolean isFirst() {
        return page == 0;
    }

    public boolean isLast() {
        return page >= totalPages() - 1;
    }
}