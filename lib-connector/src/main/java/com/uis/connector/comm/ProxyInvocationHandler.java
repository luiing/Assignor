package com.uis.connector.comm;

import android.support.annotation.NonNull;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理InvocationHandler
 * @author uis
 */
public class ProxyInvocationHandler<T> implements InvocationHandler{

    protected T target;

    public ProxyInvocationHandler(@NonNull T target) {
        this.target = target;
    }

    private ClassLoader getClassLoader(){
        return target.getClass().getClassLoader();
    }

    private Class<?>[] getInterfaces(){
        return target.getClass().getInterfaces();
    }

    public T proxy(){
        return cast(Proxy.newProxyInstance(getClassLoader(),getInterfaces(),this));
    }

    public void beforeInvoke(Method method, Object[] args){

    }

    public Object invoke(Method method, Object[] args){
        Object result = null;
        try {
            result = method.invoke(target,args);
        }catch (Throwable ex){
            ex.printStackTrace();
        }
        return result;
    }

    public void afterInvoke(Method method, Object[] args,Object result){

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args){
        beforeInvoke(method,args);
        Object result = invoke(method,args);
        afterInvoke(method,args,result);
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object obj) {
        return (T)obj;
    }
}
