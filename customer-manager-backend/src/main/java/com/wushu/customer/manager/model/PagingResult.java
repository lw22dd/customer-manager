package com.wushu.customer.manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PagingResult<T> {
    private List<T> items;
    private Long totalCount;
    private Integer currentPage;
    private Integer pageSize;
    private Integer totalPages;
    
    public PagingResult(List<T> items, Long totalCount) {
        this.items = items;
        this.totalCount = totalCount;
    }
    
    public PagingResult(List<T> items, Long totalCount, Integer currentPage, Integer pageSize) {
        this.items = items;
        this.totalCount = totalCount;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) totalCount / pageSize);
    }
}