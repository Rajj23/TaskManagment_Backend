package com.taskManagment.demo.DTO.Page;

import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {
    private List<T> content;
    private int totalPages;
    private int pageNumber;
    private long totalElements;

    public PageResponse() {}

    public PageResponse(List<T> content, int totalPages, int pageNumber, long totalElements) {
        this.content = content;
        this.totalPages = totalPages;
        this.pageNumber = pageNumber;
        this.totalElements = totalElements;
    }
}
