package com.java8.tms.common.entity;

public class Pagination {
    private int page;
    private int limit;
    private int totalPage;

    public Pagination() {
    }

    public Pagination(int page, int limit, int totalPage) {
        super();
        this.page = page;
        this.limit = limit;
        this.totalPage = totalPage;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

}
