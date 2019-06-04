package com.uis.connector.workshop;
/**
 * @author uis
 */
public abstract class SimpleObserver<T> implements ConnObserver<T> {
    @Override
    public void onResponse(Response<T> resp) {

    }

    @Override
    public void onCacheResponse(Response<T> resp) {

    }

    @Override
    public String getCacheKey() {
        return "";
    }
}
