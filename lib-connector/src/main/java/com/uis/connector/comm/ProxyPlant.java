package com.uis.connector.comm;
/**
 * @author uis
 */
public class ProxyPlant {
    public static <T> T proxyInstance(ProxyInvocationHandler<T> handler){
        return handler.proxy();
    }
}
