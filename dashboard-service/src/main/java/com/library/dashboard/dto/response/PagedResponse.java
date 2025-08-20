package com.library.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedResponse<T> {

    // Getters and Setters
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    // Static factory method for creating from Spring Data Page
    public static <T> PagedResponse<T> of(List<T> content, int page, int size, 
                                         long totalElements, int totalPages, 
                                         boolean first, boolean last) {
        return new PagedResponse<>(content, page, size, totalElements, totalPages, first, last);
    }

    public boolean hasContent() {
        return content != null && !content.isEmpty();
    }

    public int getNumberOfElements() {
        return content != null ? content.size() : 0;
    }
}