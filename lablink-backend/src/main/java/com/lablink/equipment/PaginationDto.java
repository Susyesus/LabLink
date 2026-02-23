package com.lablink.equipment;

public class PaginationDto {

    private final int page, limit, pages;
    private final long total;

    PaginationDto(int page, int limit, long total, int pages) {
        this.page = page; this.limit = limit; this.total = total; this.pages = pages;
    }

    public int getPage()   { return page; }
    public int getLimit()  { return limit; }
    public long getTotal() { return total; }
    public int getPages()  { return pages; }
}
