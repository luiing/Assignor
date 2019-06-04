/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.proxy

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * 动态代理InvocationHandler
 * @author uis 2019/5/10
 */
class ProxyHandler<T : Any>(protected var target: T) : InvocationHandler {

    private val mclassLoader: ClassLoader? get() = target::class.java.classLoader

    private val minterfaces: Array<Class<*>> get() = target::class.java.interfaces

    fun proxy(): T? {
        var proxy :T? = null
        try {
             proxy = Proxy.newProxyInstance(mclassLoader, minterfaces, this)  as T
        }catch (ex :Throwable){
            ex.printStackTrace()
        }
        return proxy
    }

    fun beforeInvoke(method: Method, args: Array<Any>) {

    }

    operator fun invoke(method: Method, args: Array<Any>): Any? {
        var result: Any? = null
        try {
            result = method.invoke(target, *args)
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }

        return result
    }

    fun afterInvoke(method: Method, args: Array<Any>, result: Any?) {

    }

    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any? {
        beforeInvoke(method, args)
        val result = invoke(method, args)
        afterInvoke(method, args, result)
        return result
    }
}
