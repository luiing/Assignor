/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.works
import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors

object Worker{

    @JvmStatic
    private val io by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { Executors.newCachedThreadPool() }
    @JvmStatic
    private val handler by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {Handler(Looper.getMainLooper())}

    @JvmStatic
    fun isMainThread() :Boolean = Looper.getMainLooper() == Looper.myLooper()

    @JvmStatic
    fun mainExecute(call :()->Unit,delay :Long=0L){
        if(isMainThread() && delay <= 0){
            mainCall(call)()
        }else{
            handler.postDelayed(mainCall(call) ,delay)
        }
    }

    @JvmStatic
    private fun mainCall(call :()->Unit):()->Unit = {
        kotlin.runCatching {
            call()
        }.exceptionOrNull()?.apply { printStackTrace() }
    }

    @JvmStatic
    fun ioExecute(call :()->Unit){
        io.submit{
            kotlin.runCatching {
                call()
            }.exceptionOrNull()?.apply { printStackTrace() }
        }
    }

    @JvmStatic
    fun asyncWork() :IAsyncWork = AsyncWorkImpl()

    @JvmStatic
    fun syncWork() :ISyncWork = SyncWorkImpl()

    @JvmStatic
    fun <T :Any> proxyInstance(handler: ProxyHandler<T>): T? {
        return handler.proxy()
    }
}