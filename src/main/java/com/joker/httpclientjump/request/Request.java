package com.joker.httpclientjump.request;

import java.util.Set;

public class Request {

    private Method method;
    private Set<String> pathAbsolute;

    public void setMethod(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public Set<String> getPathAbsolute() {
        return pathAbsolute;
    }

    public void setPathAbsolute(Set<String> pathAbsolute) {
        this.pathAbsolute = pathAbsolute;
    }
  
    public enum Method {
        /**
         * GET 方法
         */
        GET,
        /**
         * PUT 方法
         */
        PUT,
        /**
         * POST 方法
         */
        POST,
        /**
         * PATCH 方法
         */
        PATCH,
        /**
         * DELETE 方法
         */
        DELETE,
        /**
         * 支持所有方法
         */
        ALL
    }
}
