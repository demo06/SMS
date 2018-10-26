package com.lx.sms.bean;

/**
 * 公用
 */

public class BaseBean {
    public Header header;
    public Request request;
    public Response response;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return request == null ? "{" + "header:{" + header + "}}" :
                "{" + "header:{" + header + "},request:{" + request + "}}";
    }

}
