/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.uis.assignor.cache.Cache
import com.uis.assignor.cache.CacheImpl
import com.uis.assignor.utils.ALog
import com.uis.assignor.utils.MD5
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author uis
 */
object AssignorTask {
    private var application: Application? = null
    private var service: ExecutorService? = null
    private var mPoolExecutor: ThreadPoolExecutor? = null
    private val coreSize = Math.min(12, 2 * Runtime.getRuntime().availableProcessors() + 1)
    private val maxNumSize = 2 * coreSize
    private val capacitySize = 256

    private val sThreadFactory = object : ThreadFactory {
        private val mCount = AtomicInteger(1)
        override fun newThread(r: Runnable?): Thread {
            val name = "Connector Pool #" + mCount.getAndIncrement()
            ALog.print(name)
            return Thread(r, name)
        }
    }
    private val mHandler = Handler(Looper.getMainLooper())
    private val cache = CacheImpl()

    init {
        mPoolExecutor = ThreadPoolExecutor(coreSize, maxNumSize, 30L, TimeUnit.SECONDS, LinkedBlockingQueue(capacitySize), sThreadFactory)
        service = mPoolExecutor
        /** 7.0有些手机，8.0手机获取为null */
        try {
            val method = Class.forName("android.app.ActivityThread").getDeclaredMethod("currentApplication")
            application = method.invoke(null) as Application
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    fun isMainThread() :Boolean{
        return Looper.getMainLooper() == Looper.myLooper()
    }

    fun mainThread(vararg runs: Runnable,mills: Long = 0){
        for( runnable in runs) {
            try {
                mHandler.postDelayed(runnable,mills)
            }catch (ex : Throwable){
                ex.printStackTrace()
            }
        }
    }

    fun ioThread(vararg runs: Runnable) {
        for (run in runs) {
            try {
                service?.submit(run)
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }

        }
    }

    fun app(): Application? {
        return application
    }

    fun setApp(app: Application) {
        if (application == null) {
            application = app
        }
    }

    fun cache(): Cache {
        return cache
    }

    fun getFileName(prefix: String, key: String): String {
        return prefix + MD5.md5(key)
    }

    fun clearAll(prefix: String) {
        cache.clearAll(prefix)
    }

    fun clearAllMemory(prefix: String) {
        cache.clearAllMemory(prefix)
    }

    fun clearMemoryCache(key: String) {
        clearMemoryCache(Cache.NO_PREFIX, key)
    }

    fun clearMemoryCache(prefix: String, key: String) {
        if (!TextUtils.isEmpty(key)) {
            cache.clearMemory(getFileName(prefix, key))
        }
    }

    fun clearCache(key: String) {
        clearCache(Cache.NO_PREFIX, key)
    }

    fun clearCache(prefix: String, key: String) {
        if (!TextUtils.isEmpty(key)) {
            cache.clear(getFileName(prefix, key))
        }
    }

    fun writeCache(key: String, value: Any?) {
        writeCache(false, Cache.NO_PREFIX, key, value)
    }

    fun writeCache(prefix: String, key: String, value: Any?) {
        writeCache(false, prefix, key, value)
    }

    fun writeCache(isMemory: Boolean, prefix: String, key: String, value: Any?) {
        if (!TextUtils.isEmpty(key)) {
            val kk = getFileName(prefix, key)
            cache.put(kk, value)
            if (!isMemory) {
                ioThread(Runnable { cache.writeFile(kk, value) })
            }
        }
    }

    fun readCache(key: String): String? {
        return readCache(false, Cache.NO_PREFIX, key, Cache.NO_TIME_OUT.toLong())
    }

    fun readCache(key: String, mills: Long): String? {
        return readCache(false, Cache.NO_PREFIX, key, mills)
    }

    fun readCache(prefix: String, key: String): String? {
        return readCache(false, prefix, key, Cache.NO_TIME_OUT.toLong())
    }

    fun readCache(prefix: String, key: String, mills: Long): String? {
        return readCache(false, prefix, key, mills)
    }

    fun readCache(isMemory: Boolean, key: String, mills: Long): String? {
        return readCache(isMemory, Cache.NO_PREFIX, key, mills)
    }

    fun readCache(isMemory: Boolean, prefix: String, key: String, mills: Long): String? {
        var value: String? = null
        if (!TextUtils.isEmpty(key)) {
            val kk = getFileName(prefix, key)
            value = cache.get(kk, mills)
            if (!isMemory && value == null) {
                value = cache.readFile(kk, mills, true)
            }
        }
        return value
    }
}
