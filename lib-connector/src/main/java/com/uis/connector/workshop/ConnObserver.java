package com.uis.connector.workshop;

/**
 * 观察者回调接口，范型T是回调结果的类型，根据T的类型配对
 * @author uis
 * @param <T>
 */
public interface ConnObserver<T> {
    void onResponse(Response<T> resp);

    void onCacheResponse(Response<T> resp);

    String getCacheKey();
}
