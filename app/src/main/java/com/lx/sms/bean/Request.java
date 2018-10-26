package com.lx.sms.bean;


public class Request<T> {
    public T request;

    public Request(T request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return request+"";
    }
}
