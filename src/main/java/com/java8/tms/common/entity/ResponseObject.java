package com.java8.tms.common.entity;

public class ResponseObject {
    private String status;
    private String message;
    private Pagination pagination;
    private Object data;

    public ResponseObject() {
    }

    public ResponseObject(String status, String message, Pagination pagination, Object data) {
        super();
        this.status = status;
        this.message = message;
        this.pagination = pagination;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

}
