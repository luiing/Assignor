/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor

import android.app.Activity
import android.support.v4.app.Fragment
import android.text.TextUtils
import com.google.gson.Gson
import com.uis.assignor.callable.ActLifecycle
import com.uis.assignor.callable.Assignor
import com.uis.assignor.callable.NoCacheCall
import com.uis.assignor.callable.ReleaseCall
import com.uis.assignor.utils.TypeConvert
import java.lang.reflect.Type
import java.util.ArrayList

/**
 * support auto unregister when attach activity
 * @author uis
 */
class AssignorOwner {
    private val obs = ArrayList<Assignor<Any>>(4)
    private val types = ArrayList<Type>(4)
    private var activity: Activity? = null
    private var releaseCall: ReleaseCall? = null
    private var noCacheCall :NoCacheCall? = null
    private val lock = Any()

    private companion object {
        val sOwnerList = ArrayList<AssignorOwner>(4)

        init {
            initActLifecycle()
        }

        fun initActLifecycle() {
            AssignorTask.app()?.let{
                it.registerActivityLifecycleCallbacks(object : ActLifecycle() {
                    override fun onActivityDestroyed(activity: Activity) {
                        for (i in sOwnerList.indices.reversed()) {
                            val item = sOwnerList[i]
                            if (item.activity != null && item.activity === activity) {
                                sOwnerList.removeAt(i)
                                item.unregisterObservers()
                            }
                        }
                    }
                })
            }
        }
    }

    fun attach(activity: Activity): AssignorOwner {
        if (AssignorTask.app() == null) {
            AssignorTask.setApp(activity.application)
            initActLifecycle()
        }
        AssignorTask.app()?.let{
            this.activity = activity
            if (!sOwnerList.contains(this)) {
                sOwnerList.add(this)
            }
        }
        return this
    }

    fun attach(fragment: Fragment?) :AssignorOwner {
        fragment?.activity?.let {
            attach(it)
        }
        return this
    }

    fun setReleaseCall(call: ReleaseCall) :AssignorOwner{
        releaseCall = call
        return this
    }

    fun setNoCacheCall(call :NoCacheCall) :AssignorOwner{
        noCacheCall = call
        return this
    }

    /**
     * 读取存储
     * @param cacheKey
     * @param cacheMills
     * @param isMemory true:只取内存，false:内存没有取磁盘缓存
     */
    fun notifyCache(cacheKey: String, cacheMills: Long = -1, isMemory: Boolean = false) {
        AssignorTask.ioThread(CacheReadRunnable(this, cacheKey, cacheMills, isMemory))
    }

    private fun notifyCacheCall(cacheKey: String, result: String?) {
        if (!TextUtils.isEmpty(cacheKey)) {
            synchronized(lock) {
                var type: Type?
                for (i in obs.indices.reversed()) {
                    type = types[i]
                    val connObs = obs[i]
                    if (TextUtils.equals(connObs.cacheKey, cacheKey)) {
                        notifyCallback(connObs, AssignorResult(Gson().fromJson(result, type),cacheKey,true))
                    }
                }
            }
        }
    }

    fun notifyResponse(resp: AssignorResult<Any>) {
        resp.result?.let{
            val cacheKey = resp.cacheKey
            if (!TextUtils.isEmpty(cacheKey)) {
                AssignorTask.writeCache(cacheKey, resp.result)
            }
            synchronized(lock) {
                var type: Type?
                for (i in obs.indices.reversed()) {
                    type = types[i]
                    val connObs = obs[i]
                    var canObserver = !TextUtils.isEmpty(cacheKey) && TextUtils.equals(connObs.cacheKey, cacheKey)
                    if (!canObserver) {
                        val resClass = resp.result?.javaClass
                        canObserver = (type == resClass || type == resClass?.getGenericSuperclass())
                    }

                    if (canObserver) {
                        notifyCallback(connObs, resp)
                    }
                }
            }
        }
    }

    /**
     * 清除内存缓存
     * @param cacheKey
     */
    fun clearMemoryCache(cacheKey: String) {
        AssignorTask.clearMemoryCache(cacheKey)
    }

    /**
     * 清除内存和磁盘缓存
     * @param cacheKey
     */
    fun clearCache(cacheKey: String,isDisk :Boolean=false) {
        AssignorTask.clearCache(cacheKey)
    }

    private fun notifyCallback(observer: Assignor<Any>, resp: AssignorResult<Any>) {
        if (obs != null && obs.contains(observer) && resp.result != null) {
            AssignorTask.mainThread(CallbackRunnable(observer, resp))
        }
    }

    fun registerObserver(vararg observers: Assignor<out Any>) {
        registerObserver(true, -1, *observers)
    }

    fun registerObserver(isMemeory: Boolean, cacheMills: Long, vararg observers: Assignor<out Any>) {
        synchronized(lock) {
            for (observer in observers) {
                if (!obs.contains(observer)) {
                    (observer as Assignor<Any>)?.let {
                        obs.add(it)
                    }
                    TypeConvert.convert(observer)?.let {
                        types.add(it)
                    }
                }
                val key = observer.cacheKey
                if (!TextUtils.isEmpty(key)) {
                    notifyCache(key, cacheMills, isMemeory)
                }
            }
        }
    }

    fun unregisterObserver(vararg observers: Assignor<out Any>) {
        synchronized(lock) {
            for (observer in observers) {
                if (obs.contains(observer)) {
                    val index = obs.indexOf(observer)
                    if (index >= 0) {
                        obs.removeAt(index)
                        types.removeAt(index)
                    }
                }
            }
        }
    }

    fun unregisterObservers() {
        synchronized(lock) {
            obs.clear()
            types.clear()
        }
        releaseCall?.let{
            it.onRelease()
        }
        releaseCall = null
        noCacheCall = null
        activity = null
    }

    private class CallbackRunnable<T>  constructor(var observer: Assignor<T>, var resp: AssignorResult<T>) : Runnable {

        override fun run() {
            try {
                if (resp.isCache) {
                    observer.onCacheResult(resp)
                } else {
                    observer.onResult(resp)
                }
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }

        }
    }

    private class CacheReadRunnable constructor(internal var owner: AssignorOwner, internal var cacheKey: String, internal var mills: Long, internal var isMemory: Boolean) : Runnable {

        override fun run() {
            try {
                owner.notifyCacheCall(cacheKey, AssignorTask.readCache(isMemory, cacheKey, mills))
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }

        }
    }
}
