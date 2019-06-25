/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.works

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * 动态代理InvocationHandler
 * @author uis 2019/5/10
 */
@Suppress("unchecked_cast")
open class ProxyHandler<T :Any>(protected var target: T) : InvocationHandler {

    private val mclassLoader: ClassLoader? get() = target::class.java.classLoader

    private val minterfaces: Array<Class<*>> get() = target::class.java.interfaces

    fun proxy() = Proxy.newProxyInstance(mclassLoader, minterfaces, this) as T


    open fun beforeInvoke(method: Method, vararg args:Any){}

    operator fun invoke(method: Method, vararg args:Any) = method.invoke(target, args)

    open fun afterInvoke(method: Method,result:Any, vararg args:Any){}

    override fun invoke(proxy: Any, method: Method, vararg args:Any): Any{
        beforeInvoke(method, args)
        val result = invoke(method, args) as Any
        afterInvoke(method, result,args)
        return result
    }
}
