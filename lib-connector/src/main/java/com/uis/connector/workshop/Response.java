package com.uis.connector.workshop;

/**
 * 回调结果，paradigm和{@link ConnObserver}类型一致
 * @author uis
 */
public class Response<T> {
    public T result;
    public boolean isCache;
    public String cacheKey;

    public static Builder newBuilder(Object arg){
        return new Builder(arg);
    }

    private Response(){}

    public static class Builder {
        Response resp;

        public Builder(Object arg) {
            resp = new Response();
            resp.result = arg;
        }

        public Builder setCache(){
            resp.isCache = true;
            return this;
        }

        public Builder setCacheKey(String cacheKey){
            resp.cacheKey = cacheKey;
            return this;
        }

        public Response build(){
            return resp;
        }
    }
}
